package exercicio.api.dto;


import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MembroRequest {
    private String nome;
    private String email;
    private String senha;
}
