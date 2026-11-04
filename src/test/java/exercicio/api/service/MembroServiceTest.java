package exercicio.api.service;

import exercicio.api.dto.MembroRequest;
import exercicio.api.dto.MembroRequest;
import exercicio.api.dto.MembroResponse;
import exercicio.api.exceptions.CollisionArgumentException;
import exercicio.api.exceptions.MembroNotFoundException;
import exercicio.api.mapper.MembroMapper;
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

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class MembroServiceTest {

    @InjectMocks
    MembroService service;

    @Mock
    MembroRepository repository;

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
    @DisplayName("Cadastra um membro com sucesso")
    void cadastrar_retornaSucesso() {
        service.cadastrar(membroRequest);
        Mockito.verify(repository).save(Mockito.any());
    }

    @Test
    @DisplayName("Cadastra uma coleção de membros com sucesso")
    void cadastrarTodos_retornaSucesso() {
        MembroRequest request1 = MembroRequest.builder().nome("pessoa1").build();
        MembroRequest request2 = MembroRequest.builder().nome("pessoa2").build();
        MembroRequest request3 = MembroRequest.builder().nome("pessoa3").build();

        service.cadastrarTodos(List.of(request1, request2, request3));
        Mockito.verify(repository).saveAll(Mockito.any());
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
        BDDMockito.when(repository.findById(4L)).thenReturn(Optional.of(membro));

        membroRequest.setEmail("outro-email@gmail.com");
        membroRequest.setNome("nome");
        membroRequest.setSenha("91-0j1i2j5i");

        service.atualizar(4L, membroRequest);

        Assertions.assertThat(membro.getNome()).isEqualTo(membroRequest.getNome());
        Assertions.assertThat(membro.getEmail()).isEqualTo(membroRequest.getEmail());
        Assertions.assertThat(membro.getSenha()).isEqualTo(membroRequest.getSenha());
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
    @DisplayName("delete lanca erro quando id não existe")
    void delete_lancaErro_quandoIdNaoExiste() {
        BDDMockito.when(repository.findById(5L)).thenReturn(Optional.empty());

        Assertions.assertThatExceptionOfType(MembroNotFoundException.class)
                .isThrownBy(() -> service.delete(5L))
                .withMessage("Membro não encontrado");

        Mockito.verify(repository, Mockito.never()).deleteById(5L);
    }
}