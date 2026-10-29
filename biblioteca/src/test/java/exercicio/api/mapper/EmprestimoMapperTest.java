package exercicio.api.mapper;

import exercicio.api.dto.EmprestimoRequest;
import exercicio.api.dto.EmprestimoResponse;
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

class EmprestimoMapperTest {

    EmprestimoMapper mapper = Mappers.getMapper(EmprestimoMapper.class);

    Livro livro;
    Membro membro;
    Emprestimo emprestimoNaoFinalizado;
    Emprestimo emprestimoFinalizado;
    List<Emprestimo> emprestimos;
    LocalDate data;

    @BeforeEach
    void init() {
        emprestimos = List.of(Emprestimo.builder().id(7L).build());

        livro = Livro.builder()
                .id(1L)
                .emprestimos(List.of(new Emprestimo()))
                .autor("Fulano")
                .titulo("Sherlock")
                .isbn("00000-000")
                .quantidade(5)
                .build();

        membro = Membro.builder()
                .id(3L)
                .nome("Ciclano")
                .email("ciclano123@gmail.com")
                .senha("senhamuitoforte")
                .emprestimos(emprestimos)
                .dataCadastro(LocalDate.now())
                .build();


        data = LocalDate.of(2027, 4, 1);

        emprestimoNaoFinalizado = Emprestimo.builder()
                .id(2L)
                .membro(membro)
                .livro(livro)
                .dataEmprestimo(LocalDate.now())
                .dataPrevistaDevolucao(data)
                .dataDevolucao(null)
                .status(StatusEmprestimo.EMPRESTADO)
                .build();

        emprestimoFinalizado = Emprestimo.builder()
                .id(3L)
                .membro(membro)
                .livro(livro)
                .dataEmprestimo(LocalDate.now())
                .dataPrevistaDevolucao(data)
                .dataDevolucao(LocalDate.now().minusDays(1L))
                .status(StatusEmprestimo.DEVOLVIDO)
                .build();
    }


    @Test
    @DisplayName("Retorna um empréstimo com todos os campos do request preenchidos")
    void toEmprestimo_returnsAEmprestimo_WhitEmprestimoRequest() {
        EmprestimoRequest request = new EmprestimoRequest(1L, 1L, data);
        Emprestimo emprestimo = mapper.toEmprestimo(request);

        Assertions.assertThat(emprestimo.getDataDevolucao()).isNull();
        Assertions.assertThat(emprestimo.getLivro()).isNull();
        Assertions.assertThat(emprestimo.getId()).isNull();
        Assertions.assertThat(emprestimo.getMembro()).isNull();
        Assertions.assertThat(emprestimo.getStatus()).isEqualTo(StatusEmprestimo.EMPRESTADO);
        Assertions.assertThat(emprestimo.getDataPrevistaDevolucao()).isEqualTo(data);
        Assertions.assertThat(emprestimo.getDataEmprestimo()).isEqualTo(LocalDate.now());
    }

    @Test
    @DisplayName("Retorna um EmprestimoResponse com todos os dados possíveis do empréstimo não finalizado")
    void toResponse_returnsAEmprestimoResponse_withAEmprestimoNotFinalized() {
        EmprestimoResponse response = mapper.toResponse(emprestimoNaoFinalizado);

        Assertions.assertThat(response.getId()).isEqualTo(emprestimoNaoFinalizado.getId());
        Assertions.assertThat(response.getDataDevolucao()).isNull();
        Assertions.assertThat(response.getDataEmprestimo()).isEqualTo(emprestimoNaoFinalizado.getDataEmprestimo());
        Assertions.assertThat(response.getStatus()).isEqualTo(emprestimoNaoFinalizado.getStatus());
        Assertions.assertThat(response.getDataPrevistaDevolucao()).isEqualTo(emprestimoNaoFinalizado.getDataPrevistaDevolucao());
        Assertions.assertThat(response.getIdLivro()).isEqualTo(emprestimoNaoFinalizado.getLivro().getId());
        Assertions.assertThat(response.getIdMembro()).isEqualTo(emprestimoNaoFinalizado.getMembro().getId());
    }

    @Test
    @DisplayName("Retorna um EmprestimoResponse com todos os dados possíveis do empréstimo finalizado")
    void toResponse_returnsAEmprestimoResponse_withAEmprestimoFinalized() {
        EmprestimoResponse response = mapper.toResponse(emprestimoFinalizado);

        Assertions.assertThat(response.getId()).isEqualTo(emprestimoFinalizado.getId());
        Assertions.assertThat(response.getDataDevolucao()).isEqualTo(emprestimoFinalizado.getDataDevolucao());
        Assertions.assertThat(response.getDataEmprestimo()).isEqualTo(emprestimoFinalizado.getDataEmprestimo());
        Assertions.assertThat(response.getStatus()).isEqualTo(emprestimoFinalizado.getStatus());
        Assertions.assertThat(response.getDataPrevistaDevolucao()).isEqualTo(emprestimoFinalizado.getDataPrevistaDevolucao());
        Assertions.assertThat(response.getIdLivro()).isEqualTo(emprestimoFinalizado.getLivro().getId());
        Assertions.assertThat(response.getIdMembro()).isEqualTo(emprestimoFinalizado.getMembro().getId());
    }
}