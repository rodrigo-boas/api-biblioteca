package exercicio.api.dto;


import exercicio.api.model.enums.StatusEmprestimo;
import lombok.*;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
public class EmprestimoResponse {

    @EqualsAndHashCode.Include
    private Long id;
    private Long idMembro;
    private Long idLivro;
    private LocalDate dataEmprestimo;
    private LocalDate dataPrevistaDevolucao;
    private LocalDate dataDevolucao;
    private StatusEmprestimo status;


}
