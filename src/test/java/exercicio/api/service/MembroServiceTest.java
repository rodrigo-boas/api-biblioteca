package exercicio.api.service;

import exercicio.api.dto.MembroRequest;
import exercicio.api.dto.MembroResponse;
import exercicio.api.exceptions.CollisionArgumentException;
import exercicio.api.exceptions.MembroNotFoundException;
import exercicio.api.mapper.MembroMapper;
import exercicio.api.model.Emprestimo;
import exercicio.api.model.Membro;
import exercicio.api.repository.MembroRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class MembroServiceTest {

    @InjectMocks
    MembroService service;

    @Mock
    MembroRepository repository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Spy
    MembroMapper mapper = Mappers.getMapper(MembroMapper.class);


    MembroRequest membroRequest;
    List<Membro> membros;
    Membro membro;

    @BeforeEach
    void init() {
        Membro membro1 = Membro.builder().id(1L).build();
        Membro membro2 = Membro.builder().id(2L).build();
        Membro membro3 = Membro.builder().id(3L).build();

        membroRequest = MembroRequest.builder()
                .nome("pessoa")
                .senha("325912-jjhk")
                .email("pessoa123@gmail.com")
                .build();

        membros = List.of(membro1, membro2, membro3);

        membro = mapper.toMembro(membroRequest);
        membro.setId(4L);
    }

    @Test
    @DisplayName("Cadastra um membro com sucesso e senha criptografada")
    void cadastrar_retornaSucesso() {
        BDDMockito.when(passwordEncoder.encode("325912-jjhk")).thenReturn("senha-hash");

        service.cadastrar(membroRequest);

        ArgumentCaptor<Membro> captor = ArgumentCaptor.forClass(Membro.class);
        Mockito.verify(repository).save(captor.capture());
        Assertions.assertThat(captor.getValue().getSenha()).isEqualTo("senha-hash");
    }

    @Test
    @DisplayName("Cadastrar lanca erro quando email já existe")
    void cadastrar_lancaErro_quandoEmailJaExiste() {
        BDDMockito.when(repository.existsByEmail(membroRequest.getEmail())).thenReturn(true);

        Assertions.assertThatExceptionOfType(CollisionArgumentException.class)
                .isThrownBy(() -> service.cadastrar(membroRequest))
                .withMessage("O email já foi usado");

        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
        Mockito.verify(passwordEncoder, Mockito.never()).encode(Mockito.any());
    }

    @Test
    @DisplayName("Cadastra uma coleção de membros com sucesso e senhas criptografadas")
    void cadastrarTodos_cadastraComSucesso() {
        MembroRequest request1 = MembroRequest.builder().nome("pessoa1").senha("123").email("pessoa1@gmail.com").build();
        MembroRequest request2 = MembroRequest.builder().nome("pessoa2").senha("456").email("pessoa2@gmail.com").build();
        MembroRequest request3 = MembroRequest.builder().nome("pessoa3").senha("789").email("pessoa3@gmail.com").build();

        BDDMockito.when(passwordEncoder.encode("123")).thenReturn("senha-hash1");
        BDDMockito.when(passwordEncoder.encode("456")).thenReturn("senha-hash2");
        BDDMockito.when(passwordEncoder.encode("789")).thenReturn("senha-hash3");

        service.cadastrarTodos(List.of(request1, request2, request3));

        ArgumentCaptor<List<Membro>> captor = ArgumentCaptor.forClass(List.class);
        Mockito.verify(repository).saveAll(captor.capture());
        Assertions.assertThat(captor.getValue().stream().map(Membro::getSenha))
                .containsExactly("senha-hash1", "senha-hash2", "senha-hash3");
    }

    @Test
    @DisplayName("Lança erro com uma coleção de membros que contenha email duplicado na própria coleção")
    void cadastrarTodos_lancaErro_quandoPassaListaComEmailDuplicado() {
        MembroRequest request1 = MembroRequest.builder().nome("pessoa1").email("pessoa1@gmail.com").build();
        MembroRequest request2 = MembroRequest.builder().nome("pessoa2").email("duplicado@gmail.com").build();
        MembroRequest request3 = MembroRequest.builder().nome("pessoa3").email("duplicado@gmail.com").build();

        Assertions.assertThatExceptionOfType(CollisionArgumentException.class)
                .isThrownBy(() -> service.cadastrarTodos(List.of(request1, request2, request3)))
                .withMessage("Emails duplicados na lista");

        Mockito.verify(repository, Mockito.never()).saveAll(Mockito.any());
        Mockito.verify(passwordEncoder, Mockito.never()).encode(Mockito.any());
    }

    @Test
    @DisplayName("Lança erro com uma coleção de membros que contenha email duplicado com o banco")
    void cadastrarTodos_lancaErro_quandoPassaListaComEmailDuplicadoNoBanco() {
        MembroRequest request1 = MembroRequest.builder().nome("pessoa1").email("pessoa1@gmail.com").build();
        MembroRequest request2 = MembroRequest.builder().nome("pessoa2").email("pessoa2@gmail.com").build();
        MembroRequest request3 = MembroRequest.builder().nome("pessoa3").email("pessoa3@gmail.com").build();

        List<MembroRequest> list = List.of(request1, request2, request3);

        BDDMockito.when(repository.existsByEmailIn(Mockito.any())).thenReturn(true);

        Assertions.assertThatExceptionOfType(CollisionArgumentException.class)
                .isThrownBy(() -> service.cadastrarTodos(list))
                .withMessage("Email já existe");

        Mockito.verify(repository, Mockito.never()).saveAll(Mockito.any());
        Mockito.verify(passwordEncoder, Mockito.never()).encode(Mockito.any());
    }

    @Test
    @DisplayName("Deve retornar uma lista de response com todos os membros cadastrados")
    void listAll_retornaListaDeMembroResponse() {
        BDDMockito.when(repository.findAll()).thenReturn(membros);

        Assertions.assertThat(service.listAll())
                .extracting(MembroResponse::getId).containsExactly(1L, 2L, 3L);
    }

    @Test
    @DisplayName("Deve retornar um MembroResponse quando o id existir")
    void findById_retornaUmMembroResponse_quandoIdExiste() {
        BDDMockito.when(repository.findById(4L)).thenReturn(Optional.of(membro));

        MembroResponse byId = service.findById(4L);

        Assertions.assertThat(byId).isNotNull();
    }

    @Test
    @DisplayName("findById lança exceção quando id não existe")
    void findById_lancaErro_quandoIdNaoExiste() {
        BDDMockito.when(repository.findById(4L)).thenReturn(Optional.empty());

        Assertions.assertThatExceptionOfType(MembroNotFoundException.class)
                .isThrownBy(() -> service.findById(4L))
                .withMessage("Membro não encontrado");
    }

    @Test
    @DisplayName("Membro é atualizado corretamente")
    void atualizar_atualizaOsValores_quandoIdExiste() {
        membroRequest.setEmail("outro-email@gmail.com");
        membroRequest.setNome("nome");
        membroRequest.setSenha("91-0j1i2j5i");

        BDDMockito.when(passwordEncoder.encode(membroRequest.getSenha())).thenReturn("senha-hash");
        BDDMockito.when(repository.findById(4L)).thenReturn(Optional.of(membro));

        service.atualizar(4L, membroRequest);

        Assertions.assertThat(membro.getNome()).isEqualTo(membroRequest.getNome());
        Assertions.assertThat(membro.getEmail()).isEqualTo(membroRequest.getEmail());
        Assertions.assertThat(membro.getSenha()).isEqualTo("senha-hash");
    }

    @Test
    @DisplayName("atualizar lança exceção quando id não existe")
    void atualizar_lancaErro_quandoIdNaoExiste() {
        BDDMockito.when(repository.findById(4L)).thenReturn(Optional.empty());

        membroRequest.setEmail("outro-email@gmail.com");
        membroRequest.setNome("nome");
        membroRequest.setSenha("91-0j1i2j5i");

        Assertions.assertThatExceptionOfType(MembroNotFoundException.class)
                .isThrownBy(() -> service.atualizar(4L, membroRequest))
                .withMessage("Membro não encontrado");
    }

    @Test
    @DisplayName("atualizar lança exceção quando email já existe")
    void atualizar_lancaErro_quandoEmailJaExiste() {
        BDDMockito.when(repository.findById(4L)).thenReturn(Optional.of(membro));
        BDDMockito.when(repository.existsByEmailAndIdNot("outro-email@gmail.com", 4L))
                .thenReturn(true);

        membroRequest.setEmail("outro-email@gmail.com");
        membroRequest.setNome("nome");
        membroRequest.setSenha("91-0j1i2j5i");

        Assertions.assertThatExceptionOfType(CollisionArgumentException.class)
                .isThrownBy(() -> service.atualizar(4L, membroRequest))
                .withMessage("O email já foi usado");
    }

    @Test
    @DisplayName("Deve deletar um membro com sucesso")
    void delete_deletaUmMembroComSucesso() {
        BDDMockito.when(repository.findById(4L)).thenReturn(Optional.of(membro));

        service.delete(4L);

        Mockito.verify(repository).deleteById(4L);
    }

    @Test
    @DisplayName("delete lanca erro quando membro possui emprestimo")
    void delete_lancaErro_quandoEmprestimoExiste() {
        membro.setEmprestimos(List.of(new Emprestimo()));

        BDDMockito.when(repository.findById(4L)).thenReturn(Optional.of(membro));

        Assertions.assertThatExceptionOfType(CollisionArgumentException.class)
                .isThrownBy(() -> service.delete(4L))
                .withMessage("O membro não pode ser deletado por possuir empréstimos registrados");

        Mockito.verify(repository, Mockito.never()).deleteById(4L);
    }
}