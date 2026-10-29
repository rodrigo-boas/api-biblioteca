package exercicio.api.dto;


import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Builder
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
public class MembroResponse {

    @EqualsAndHashCode.Include
    private Long id;
    private String nome;
    private String email;
    private LocalDate dataCadastro;
    private List<Long> idEmprestimos;

}
