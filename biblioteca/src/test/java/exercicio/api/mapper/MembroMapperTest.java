package exercicio.api.mapper;

import exercicio.api.dto.MembroRequest;
import exercicio.api.dto.MembroResponse;
import exercicio.api.model.Emprestimo;
import exercicio.api.model.Membro;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MembroMapperTest {

    MembroMapper mapper = Mappers.getMapper(MembroMapper.class);
    Membro membro;

    List<Emprestimo> emprestimos;


    @BeforeEach
    void init() {
        emprestimos = List.of(Emprestimo.builder().id(2L).build(), Emprestimo.builder().id(11L).build());

        membro = Membro.builder()
                .id(3L)
                .nome("Ciclano")
                .email("ciclano123@gmail.com")
                .senha("senhamuitoforte")
                .emprestimos(emprestimos)
                .dataCadastro(LocalDate.now())
                .build();
    }

    @Test
    @DisplayName("Retorna um Membro a partir de um MembroRequest com todos os campos do request preenchidos")
    void toMembro() {
        MembroRequest request = new MembroRequest("Rodrigo", "rodrigo@gmail.com", "461246");
        Membro membroTransformado = mapper.toMembro(request);

        Assertions.assertThat(membroTransformado.getId()).isNull();
        Assertions.assertThat(membroTransformado.getNome()).isEqualTo(request.getNome());
        Assertions.assertThat(membroTransformado.getEmail()).isEqualTo(request.getEmail());
        Assertions.assertThat(membroTransformado.getSenha()).isEqualTo(request.getSenha());
        Assertions.assertThat(membroTransformado.getDataCadastro()).isEqualTo(LocalDate.now());
        Assertions.assertThat(membroTransformado.getEmprestimos()).isEmpty();
    }

    @Test
    @DisplayName("Retorna um MembroResponse a partir de um Membro com todos os campos do response preenchidos")
    void toResponse() {
        MembroResponse response = mapper.toResponse(membro);

        Assertions.assertThat(response.getId()).isEqualTo(membro.getId());
        Assertions.assertThat(response.getDataCadastro()).isEqualTo(membro.getDataCadastro());
        Assertions.assertThat(response.getNome()).isEqualTo(membro.getNome());
        Assertions.assertThat(response.getEmail()).isEqualTo(membro.getEmail());
        Assertions.assertThat(response.getIdEmprestimos()).containsExactly(2L, 11L);
    }

    @Test
    @DisplayName("Retorna uma lista vazia caso não passe null para lista")
    void getIdList() {
        Membro build = Membro.builder().id(10L).build();
        build.setEmprestimos(null);
        MembroResponse response = mapper.toResponse(build);

        Assertions.assertThat(response.getIdEmprestimos()).isEmpty();
    }
}