package exercicio.api.service;

import exercicio.api.dto.LivroRequest;
import exercicio.api.dto.LivroResponse;
import exercicio.api.exceptions.CollisionArgumentException;
import exercicio.api.exceptions.LivroNotFoundException;
import exercicio.api.exceptions.MembroNotFoundException;
import exercicio.api.mapper.LivroMapper;
import exercicio.api.model.Emprestimo;
import exercicio.api.model.Livro;
import exercicio.api.model.enums.StatusEmprestimo;
import exercicio.api.repository.LivroRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class LivroService {

    private final LivroMapper mapper;
    private final LivroRepository repository;

    @Transactional
    public void cadastrar(LivroRequest livro) {
        Livro cadastro = mapper.toLivro(livro);
        repository.save(cadastro);
    }

    @Transactional
    public void cadastrarTodos(List<LivroRequest> livros) {
        List<Livro> list = livros.stream().map(mapper::toLivro).toList();
        repository.saveAll(list);
    }

    @Transactional(readOnly = true)
    public List<LivroResponse> listAll() {
        return repository.findAll().stream().map(mapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public LivroResponse findById(Long id) {
        Livro livro = repository.findById(id).orElseThrow(LivroNotFoundException::new);
        return mapper.toResponse(livro);
    }

    @Transactional
    public void atualizar(Long id, LivroRequest livroRequest) {
        Livro livro = repository.findById(id).orElseThrow(LivroNotFoundException::new);

        Livro livroFinal = mapper.toLivro(livroRequest);

        livro.setQuantidade(livroFinal.getQuantidade());
        livro.setAutor(livroFinal.getAutor());
        livro.setIsbn(livroFinal.getIsbn());
        livro.setTitulo(livroFinal.getTitulo());
    }

    @Transactional
    public void delete(Long id) {
        List<Emprestimo> emprestimos = repository.findById(id)
                .orElseThrow(LivroNotFoundException::new).getEmprestimos();

        if (!emprestimos.isEmpty()) {
            throw new CollisionArgumentException("O livro não pode ser deletado por possuir empréstimos registrados");
        }

        repository.deleteById(id);
    }
}
