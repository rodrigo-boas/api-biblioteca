package exercicio.api.dto;

import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
public class LivroResponse {
    @EqualsAndHashCode.Include
    private Long id;
    private String titulo;
    private String autor;
    private String isbn;
    private Integer quantidade;
    private List<Long> idEmprestimos;
}
