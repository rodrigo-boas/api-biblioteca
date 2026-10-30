package exercicio.api.service;

import exercicio.api.dto.LivroRequest;
import exercicio.api.dto.LivroResponse;
import exercicio.api.exceptions.LivroNotFoundException;
import exercicio.api.mapper.LivroMapper;
import exercicio.api.model.Livro;
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
    private final LivroRepository livroRepository;

    @Transactional
    public void cadastrar(LivroRequest livro) {
        Livro cadastro = mapper.toLivro(livro);
        livroRepository.save(cadastro);
    }

    @Transactional
    public void cadastrarTodos(List<LivroRequest> livros) {
        List<Livro> list = livros.stream().map(mapper::toLivro).toList();
        livroRepository.saveAll(list);
    }

    @Transactional(readOnly = true)
    public List<LivroResponse> listAll() {
        return livroRepository.findAll().stream().map(mapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public LivroResponse findById(Long id) {
        Livro livro = livroRepository.findById(id).orElseThrow(LivroNotFoundException::new);
        return mapper.toResponse(livro);
    }

    @Transactional
    public void atualizar(Long id, LivroRequest livroRequest) {
        Livro livro = livroRepository.findById(id).orElseThrow(LivroNotFoundException::new);

        Livro livroFinal = mapper.toLivro(livroRequest);

        livro.setQuantidade(livroFinal.getQuantidade());
        livro.setAutor(livroFinal.getAutor());
        livro.setIsbn(livroFinal.getIsbn());
        livro.setTitulo(livroFinal.getTitulo());
    }

    @Transactional
    public void delete(Long id) {
        livroRepository.findById(id).orElseThrow(LivroNotFoundException::new);
        livroRepository.deleteById(id);
    }
}
