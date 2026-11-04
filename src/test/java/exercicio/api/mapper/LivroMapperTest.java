package exercicio.api.mapper;

import exercicio.api.dto.LivroRequest;
import exercicio.api.dto.LivroResponse;
import exercicio.api.model.Emprestimo;
import exercicio.api.model.Livro;
import exercicio.api.model.Membro;
import exercicio.api.model.enums.StatusEmprestimo;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LivroMapperTest {

    LivroMapper mapper = Mappers.getMapper(LivroMapper.class);
    Livro livro;

    List<Emprestimo> emprestimos;


    @BeforeEach
    void init() {
        emprestimos = List.of(Emprestimo.builder().id(7L).build(), Emprestimo.builder().id(3L).build());

        livro = Livro.builder()
                .id(1L)
                .emprestimos(emprestimos)
                .autor("Fulano")
                .titulo("Sherlock")
                .isbn("00000-000")
                .quantidade(5)
                .build();
    }

    @Test
    @DisplayName("Retorna um Livro a partir de um LivroRequest com todos os campos do request preenchidos")
    void toLivro() {
        LivroRequest request = new LivroRequest("Um livro muito legal", "João", "111111-111", 3);
        Livro livroTransformado = mapper.toLivro(request);

        Assertions.assertThat(livroTransformado.getId()).isNull();
        Assertions.assertThat(livroTransformado.getTitulo()).isEqualTo(request.getTitulo());
        Assertions.assertThat(livroTransformado.getIsbn()).isEqualTo(request.getIsbn());
        Assertions.assertThat(livroTransformado.getAutor()).isEqualTo(request.getAutor());
        Assertions.assertThat(livroTransformado.getQuantidade()).isEqualTo(request.getQuantidade());
        Assertions.assertThat(livroTransformado.getEmprestimos()).isEmpty();
    }

    @Test
    @DisplayName("Retorna um LivroResponse a partir de um Livro com todos os campos do response preenchidos")
    void toResponse() {
        LivroResponse response = mapper.toResponse(livro);

        Assertions.assertThat(response.getId()).isEqualTo(livro.getId());
        Assertions.assertThat(response.getTitulo()).isEqualTo(livro.getTitulo());
        Assertions.assertThat(response.getIsbn()).isEqualTo(livro.getIsbn());
        Assertions.assertThat(response.getAutor()).isEqualTo(livro.getAutor());
        Assertions.assertThat(response.getQuantidade()).isEqualTo(livro.getQuantidade());
        Assertions.assertThat(response.getIdEmprestimos()).containsExactly(7L, 3L);
    }

    @Test
    @DisplayName("Retorna uma lista vazia caso não passe null para lista")
    void getIdList() {
        Livro build = Livro.builder().id(10L).build();
        build.setEmprestimos(null);
        LivroResponse response = mapper.toResponse(build);

        Assertions.assertThat(response.getIdEmprestimos()).isEmpty();
    }

    @Test
    @DisplayName("Retorna um livro com quantidade == 0 quando quantidade == null")
    void toLivro_retornaLivroComQuantidadeZero() {
        LivroRequest request = new LivroRequest();
        request.setQuantidade(null);
        Livro livro = mapper.toLivro(request);

        Assertions.assertThat(livro.getQuantidade()).isEqualTo(0);
    }
}