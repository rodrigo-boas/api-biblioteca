package exercicio.api.controller;

import exercicio.api.dto.MembroRequest;
import exercicio.api.dto.MembroResponse;
import exercicio.api.service.MembroService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/membros")
@RequiredArgsConstructor
@Validated
public class MembroController {

    private final MembroService service;

    @GetMapping
    public ResponseEntity<List<MembroResponse>> listAll() {
        return ResponseEntity.ok().body(service.listAll());
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<Void> cadastrar(@Valid @RequestBody MembroRequest membroRequest) {
        service.cadastrar(membroRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/cadastrar/varios")
    public ResponseEntity<Void> cadastrarTodos(@RequestBody List<@Valid MembroRequest> membroRequests) {
        service.cadastrarTodos(membroRequests);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MembroResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok().body(service.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> atualizar(@PathVariable Long id, @Valid @RequestBody MembroRequest membro) {
        service.atualizar(id, membro);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
