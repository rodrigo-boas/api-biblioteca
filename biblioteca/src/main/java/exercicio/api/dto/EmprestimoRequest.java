package exercicio.api.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmprestimoRequest {

    @NotNull(message = "idLivro não pode ser vazio")
    private Long idLivro;

    @NotNull(message = "idLivro não pode ser vazio")
    private Long idMembro;

    @NotNull(message = "Data não pode ser vazio")
    private LocalDate dataPrevistaDevolucao;


}
