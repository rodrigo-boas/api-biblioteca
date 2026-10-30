package exercicio.api.controller;

import exercicio.api.dto.EmprestimoRequest;
import exercicio.api.dto.EmprestimoResponse;
import exercicio.api.service.EmprestimoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/emprestimos")
@RequiredArgsConstructor
public class EmprestimoController {

    private final EmprestimoService service;

    @GetMapping
    public ResponseEntity<List<EmprestimoResponse>> listByMemberId(@RequestParam Long membroId) {
        List<EmprestimoResponse> emprestimoResponses = service.listByMemberId(membroId);
        return ResponseEntity.ok().body(emprestimoResponses);
    }

    @PatchMapping("/{id}/devolucao")
    public ResponseEntity<Void> registrarDevolucao(@PathVariable Long id) {
        service.registrarDevolucao(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<Void> realizarEmprestimo(@Valid @RequestBody EmprestimoRequest request) {
        service.realizarEmprestimo(request);
        return ResponseEntity.noContent().build();
    }
}
