package exercicio.api.mapper;

import exercicio.api.dto.LivroRequest;
import exercicio.api.dto.LivroResponse;
import exercicio.api.model.Emprestimo;
import exercicio.api.model.Livro;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface LivroMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "emprestimos", ignore = true)
    @Mapping(source = "quantidade", target = "quantidade", defaultValue = "0")
    Livro toLivro(LivroRequest livroRequest);

    @Mapping(source = "emprestimos", target = "idEmprestimos", qualifiedByName = "getIdList")
    LivroResponse toResponse(Livro livro);

    @Named("getIdList")
    default List<Long> getIdList(List<Emprestimo> emprestimos) {
        if(emprestimos == null) return new ArrayList<>();
        return emprestimos.stream().map(Emprestimo::getId).toList();
    }
}
