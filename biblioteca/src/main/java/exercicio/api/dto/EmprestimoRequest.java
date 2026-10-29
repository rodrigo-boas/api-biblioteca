package exercicio.api.dto;


import lombok.*;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmprestimoRequest {

    private Long idLivro;
    private Long idMembro;
    private LocalDate dataPrevistaDevolucao;


}
