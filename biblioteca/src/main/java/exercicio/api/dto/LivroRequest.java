package exercicio.api.dto;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LivroRequest {
    private String titulo;
    private String autor;
    private String isbn;
    private Integer quantidade;
}
