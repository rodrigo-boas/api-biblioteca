package exercicio.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LivroRequest {

    @NotBlank(message = "Título não pode ser vazio")
    private String titulo;

    @NotBlank(message = "Autor não pode ser vazio")
    private String autor;

    @NotBlank(message = "ISBN não pode ser vazio")
    private String isbn;

    @Builder.Default
    @Min(value = 0, message = "Quantidade não pode ser negativa")
    private Integer quantidade = 0;
}
