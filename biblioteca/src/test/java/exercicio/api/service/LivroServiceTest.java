package exercicio.api.service;

import exercicio.api.dto.LivroRequest;
import exercicio.api.dto.LivroResponse;
import exercicio.api.exceptions.LivroNotFoundException;
import exercicio.api.mapper.LivroMapper;
import exercicio.api.model.Livro;
import exercicio.api.repository.LivroRepository;
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
class LivroServiceTest {

    @InjectMocks
    LivroService service;

    @Mock
    LivroRepository repository;

    @Spy
    LivroMapper mapper = Mappers.getMapper(LivroMapper.class);


    LivroRequest livroRequest;
    List<Livro> livros;
    Livro livro;

    @BeforeEach
    void init() {
        Livro livro1 = Livro.builder().id(1L).build();
        Livro livro2 = Livro.builder().id(2L).build();
        Livro livro3 = Livro.builder().id(3L).build();

        livroRequest = LivroRequest.builder()
                .autor("pessoa")
                .isbn("91230")
                .titulo("titulo")
                .quantidade(2).build();

        livros = List.of(livro1, livro2, livro3);

        livro = mapper.toLivro(livroRequest);
        livro.setId(4L);
    }

    @Test
    @DisplayName("Cadastra um livro com sucesso")
    void cadastrar_retornaSucesso() {
        service.cadastrar(livroRequest);
        Mockito.verify(repository).save(Mockito.any());
    }

    @Test
    @DisplayName("Cadastra uma coleção de livros com sucesso")
    void cadastrarTodos_retornaSucesso() {
        LivroRequest request1 = LivroRequest.builder().autor("autor1").build();
        LivroRequest request2 = LivroRequest.builder().autor("autor2").build();
        LivroRequest request3 = LivroRequest.builder().autor("autor3").build();

        service.cadastrarTodos(List.of(request1, request2, request3));
        Mockito.verify(repository).saveAll(Mockito.any());
    }

    @Test
    @DisplayName("Deve retornar uma lista de response com todos os livros cadastrados")
    void listAll_retornaListaDeLivroResponse() {


        BDDMockito.when(repository.findAll()).thenReturn(livros);

        Assertions.assertThat(service.listAll())
                .extracting(LivroResponse::getId).containsExactly(1L, 2L, 3L);
    }

    @Test
    @DisplayName("Deve retornar um LivroResponse quando o id existir")
    void findById_retornaUmLivroResponse_quandoIdExiste() {
        BDDMockito.when(repository.findById(4L)).thenReturn(Optional.of(livro));

        LivroResponse byId = service.findById(4L);

        Assertions.assertThat(byId).isNotNull();
    }

    @Test
    @DisplayName("findById lança exceção quaando id não existe")
    void findById_lancaErro_quandoIdNaoExiste() {
        BDDMockito.when(repository.findById(4L)).thenReturn(Optional.empty());

        Assertions.assertThatExceptionOfType(LivroNotFoundException.class)
                .isThrownBy(() -> service.findById(4L))
                .withMessage("Livro não encontrado");
    }

    @Test
    @DisplayName("Livro é atualizado corretamente")
    void atualizar_atualizaOsValores_quandoIdExiste() {
        BDDMockito.when(repository.findById(4L)).thenReturn(Optional.of(livro));

        livroRequest.setTitulo("titulo2");
        livroRequest.setQuantidade(5);
        livroRequest.setAutor("autor2");
        livroRequest.setIsbn("12308");

        service.atualizar(4L, livroRequest);

        Assertions.assertThat(livro.getAutor()).isEqualTo(livroRequest.getAutor());
        Assertions.assertThat(livro.getQuantidade()).isEqualTo(livroRequest.getQuantidade());
        Assertions.assertThat(livro.getIsbn()).isEqualTo(livroRequest.getIsbn());
        Assertions.assertThat(livro.getTitulo()).isEqualTo(livroRequest.getTitulo());
    }

    @Test
    @DisplayName("atualizar lança exceção quando id não existe")
    void atualizar_lancaErro_quandoIdNaoExiste() {
        BDDMockito.when(repository.findById(4L)).thenReturn(Optional.empty());

        livroRequest.setTitulo("titulo2");
        livroRequest.setQuantidade(5);
        livroRequest.setAutor("autor2");
        livroRequest.setIsbn("12308");

        Assertions.assertThatExceptionOfType(LivroNotFoundException.class)
                .isThrownBy(() -> service.atualizar(4L, livroRequest))
                .withMessage("Livro não encontrado");
    }

    @Test
    @DisplayName("Deve deletar um livro com sucesso")
    void delete_deletaUmLivroComSucesso() {
        BDDMockito.when(repository.findById(4L)).thenReturn(Optional.of(livro));

        service.delete(4L);

        Mockito.verify(repository).deleteById(4L);
    }

    @Test
    @DisplayName("delete lanca erro quando id não existe")
    void delete_lancaErro_quandoIdNaoExiste() {
        BDDMockito.when(repository.findById(5L)).thenReturn(Optional.empty());

        Assertions.assertThatExceptionOfType(LivroNotFoundException.class)
                .isThrownBy(() -> service.delete(5L))
                .withMessage("Livro não encontrado");

        Mockito.verify(repository, Mockito.never()).deleteById(5L);
    }
}