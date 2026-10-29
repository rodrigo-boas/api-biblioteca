package exercicio.api.service;

import exercicio.api.dto.EmprestimoRequest;
import exercicio.api.dto.EmprestimoResponse;
import exercicio.api.exceptions.EmprestimoIlegalException;
import exercicio.api.exceptions.EmprestimoNotFoundException;
import exercicio.api.exceptions.LivroNotFoundException;
import exercicio.api.exceptions.MembroNotFoundException;
import exercicio.api.mapper.EmprestimoMapper;
import exercicio.api.model.Emprestimo;
import exercicio.api.model.Livro;
import exercicio.api.model.Membro;
import exercicio.api.model.enums.StatusEmprestimo;
import exercicio.api.repository.EmprestimoRepository;
import exercicio.api.repository.LivroRepository;
import exercicio.api.repository.MembroRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@ExtendWith(MockitoExtension.class)
class EmprestimoServiceTest {

    @InjectMocks
    EmprestimoService service;


    @Spy
    EmprestimoMapper mapper = Mappers.getMapper(EmprestimoMapper.class);

    @Mock
    EmprestimoRepository emprestimoRepository;
    @Mock
    MembroRepository membroRepository;
    @Mock
    LivroRepository livroRepository;

    Membro membro;
    Livro livro;
    EmprestimoRequest request;
    Emprestimo emprestimo;

    @BeforeEach
    void init() {
        livro = new Livro();
        livro.setId(1L);
        livro.setQuantidade(3);

        emprestimo = new Emprestimo();

        membro = new Membro();
        membro.setId(2L);

        request = new EmprestimoRequest();
        request.setIdMembro(2L);
        request.setIdLivro(1L);
    }

    @Test
    @DisplayName("Deve realizar um empréstimo com sucesso")
    void realizarEmprestimo_retornaSucesso() {
        BDDMockito.when(livroRepository.findById(1L)).thenReturn(Optional.of(livro));
        BDDMockito.when(membroRepository.findById(2L)).thenReturn(Optional.of(membro));

        service.realizarEmprestimo(request);

        ArgumentCaptor<Emprestimo> emprestimoCapted = ArgumentCaptor.forClass(Emprestimo.class);
        Mockito.verify(emprestimoRepository).save(emprestimoCapted.capture());
        emprestimo = emprestimoCapted.getValue();

        Assertions.assertThat(emprestimo.getMembro()).isSameAs(membro);
        Assertions.assertThat(emprestimo.getLivro()).isSameAs(livro);
        Assertions.assertThat(livro.getQuantidade()).isEqualTo(2);
    }

    @Test
    @DisplayName("Deve retornar erro ao livro estar sem estoque")
    void realizarEmprestimo_retornaErro_aoLivroForaDeEstoque() {
        livro.setQuantidade(0);

        BDDMockito.when(livroRepository.findById(1L)).thenReturn(Optional.of(livro));
        BDDMockito.when(membroRepository.findById(2L)).thenReturn(Optional.of(membro));

        Assertions.assertThatExceptionOfType(EmprestimoIlegalException.class).
                isThrownBy(() -> service.realizarEmprestimo(request)).withMessage("Livro sem estoque");
        Mockito.verify(emprestimoRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    @DisplayName("Deve retornar erro ao membro possuir 3 ou mais empréstimos")
    void realizarEmprestimo_retornaErro_aoMembroPossuir3OuMaisEmprestimos() {
        Emprestimo emprestimo1 = new Emprestimo();
        Emprestimo emprestimo2 = new Emprestimo();
        Emprestimo emprestimo3 = new Emprestimo();

        emprestimo1.setStatus(StatusEmprestimo.EMPRESTADO);
        emprestimo2.setStatus(StatusEmprestimo.EMPRESTADO);
        emprestimo3.setStatus(StatusEmprestimo.EMPRESTADO);

        membro.setEmprestimos(List.of(emprestimo1, emprestimo2, emprestimo3));

        BDDMockito.when(livroRepository.findById(1L)).thenReturn(Optional.of(livro));
        BDDMockito.when(membroRepository.findById(2L)).thenReturn(Optional.of(membro));

        Assertions.assertThatExceptionOfType(EmprestimoIlegalException.class).
                isThrownBy(() -> service.realizarEmprestimo(request))
                .withMessage("Membro com três empréstimos ativos");
        Mockito.verify(emprestimoRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    @DisplayName("Deve retornar sucesso ao realizar devolução")
    void registrarDevolucao_retornaSucesso() {
        emprestimo.setLivro(livro);

        BDDMockito.when(emprestimoRepository.findById(4L)).thenReturn(Optional.of(emprestimo));

        service.registrarDevolucao(4L);

        Assertions.assertThat(livro.getQuantidade()).isEqualTo(4);
        Assertions.assertThat(emprestimo.getStatus()).isEqualTo(StatusEmprestimo.DEVOLVIDO);
        Assertions.assertThat(emprestimo.getDataDevolucao()).isEqualTo(LocalDate.now());
    }

    @Test
    @DisplayName("Deve retornar erro ao empréstimo já ter sido devolvido")
    void realizarEmprestimo_retornaErro_quandoEmprestimoJaFoiDevolvido() {
        emprestimo.setLivro(livro);
        emprestimo.setStatus(StatusEmprestimo.DEVOLVIDO);

        BDDMockito.when(emprestimoRepository.findById(4L)).thenReturn(Optional.of(emprestimo));

        Assertions.assertThatExceptionOfType(EmprestimoIlegalException.class)
                .isThrownBy(() -> service.registrarDevolucao(4L))
                .withMessage("Empréstimo já foi devolvido");
    }

    @Test
    @DisplayName("Deve retornar erro quando id do livro não existe")
    void realizarEmprestimo_retornaErro_quandoLivroNaoExiste() {
        BDDMockito.when(livroRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThatExceptionOfType(LivroNotFoundException.class)
                .isThrownBy(() -> service.realizarEmprestimo(request))
                .withMessage("Livro não encontrado");
        Mockito.verify(emprestimoRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    @DisplayName("Deve retornar erro quando id do emprestimo não existe")
    void registrarDevolucao_retornaErro_quandoEmprestimoNaoExiste() {
        BDDMockito.when(emprestimoRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThatExceptionOfType(EmprestimoNotFoundException.class)
                .isThrownBy(() -> service.registrarDevolucao(1L))
                .withMessage("Emprestimo não encontrado");
        Mockito.verify(emprestimoRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    @DisplayName("Deve retornar erro quando id do membro não existe")
    void registrarDevolucao_retornaErro_quandoMembroNaoExiste() {
        BDDMockito.when(livroRepository.findById(1L)).thenReturn(Optional.of(livro));
        BDDMockito.when(membroRepository.findById(2L)).thenReturn(Optional.empty());

        Assertions.assertThatExceptionOfType(MembroNotFoundException.class)
                .isThrownBy(() -> service.realizarEmprestimo(request))
                .withMessage("Membro não encontrado");
        Mockito.verify(emprestimoRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    @DisplayName("Deve retornar sucesso ao listar emprestimos pelo id do membro")
    void listByMemberId_retornaSucesso() {
        Emprestimo emprestimo1 = new Emprestimo();
        Emprestimo emprestimo2 = new Emprestimo();
        emprestimo1.setId(4L);
        emprestimo2.setId(6L);

        membro.setEmprestimos(List.of(emprestimo1, emprestimo2));

        BDDMockito.when(membroRepository.findById(2L)).thenReturn(Optional.of(membro));

        List<EmprestimoResponse> emprestimoResponses = service.listByMemberId(2L);

        Assertions.assertThat(emprestimoResponses)
                .extracting(EmprestimoResponse::getId)
                .containsExactly(4L, 6L);
    }
}