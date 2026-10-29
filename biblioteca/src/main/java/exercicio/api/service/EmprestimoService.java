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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmprestimoService {

    private final EmprestimoMapper mapper;
    private final EmprestimoRepository emprestimoRepository;
    private final MembroRepository membroRepository;
    private final LivroRepository livroRepository;

    @Transactional
    public void realizarEmprestimo(EmprestimoRequest emprestimoRequest) {
        Livro livro = livroRepository.findById(emprestimoRequest.getIdLivro())
                .orElseThrow(LivroNotFoundException::new);

        Membro membro = membroRepository.findById(emprestimoRequest.getIdMembro())
                .orElseThrow(MembroNotFoundException::new);

        Emprestimo emprestimo = mapper.toEmprestimo(emprestimoRequest);

        long emprestimosAtivos = membro.getEmprestimos().stream()
                .filter(e -> e.getStatus() == StatusEmprestimo.EMPRESTADO)
                .count();

        if (livro.getQuantidade() < 1) throw new EmprestimoIlegalException("Livro sem estoque");
        else if (emprestimosAtivos >= 3) throw new EmprestimoIlegalException("Membro com três empréstimos ativos");

        emprestimo.setLivro(livro);
        emprestimo.setMembro(membro);
        livro.setQuantidade(livro.getQuantidade() - 1);
        emprestimoRepository.save(emprestimo);
        log.info("Empréstimo realizado com sucesso");
    }

    @Transactional
    public void registrarDevolucao(Long id) {
        Emprestimo emprestimo = emprestimoRepository.findById(id)
                .orElseThrow(EmprestimoNotFoundException::new);

        if (emprestimo.getStatus() == StatusEmprestimo.DEVOLVIDO) {
            throw new EmprestimoIlegalException("Empréstimo já foi devolvido");
        }
        Livro livro = emprestimo.getLivro();

        livro.setQuantidade(livro.getQuantidade() + 1);
        emprestimo.setStatus(StatusEmprestimo.DEVOLVIDO);
        emprestimo.setDataDevolucao(LocalDate.now());
    }

    @Transactional(readOnly = true)
    public List<EmprestimoResponse> listByMemberId(Long id) {
        Membro membro = membroRepository.findById(id).orElseThrow(MembroNotFoundException::new);
        return membro.getEmprestimos().stream().map(mapper::toResponse).toList();
    }
}
