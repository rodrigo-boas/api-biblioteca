package exercicio.api.controller;

import exercicio.api.dto.LivroRequest;
import exercicio.api.dto.LivroResponse;
import exercicio.api.mapper.LivroMapper;
import exercicio.api.model.Livro;
import exercicio.api.service.LivroService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/livros")
@RequiredArgsConstructor
@Validated
public class LivroController {

    private final LivroService service;

    @GetMapping
    public ResponseEntity<List<LivroResponse>> listAll() {
        return ResponseEntity.ok().body(service.listAll());
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<Void> cadastrar(@Valid @RequestBody LivroRequest livroRequest) {
        service.cadastrar(livroRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/cadastrar/varios")
    public ResponseEntity<Void> cadastrarTodos(@RequestBody List<@Valid LivroRequest> livroRequests) {
        service.cadastrarTodos(livroRequests);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<LivroResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok().body(service.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> atualizar(@PathVariable Long id, @Valid @RequestBody LivroRequest livro) {
        service.atualizar(id, livro);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
