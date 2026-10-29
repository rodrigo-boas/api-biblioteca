package exercicio.api.mapper;

import exercicio.api.dto.MembroRequest;
import exercicio.api.dto.MembroResponse;
import exercicio.api.model.Emprestimo;
import exercicio.api.model.Membro;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", imports = LocalDate.class, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface MembroMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dataCadastro", expression = "java(LocalDate.now())")
    @Mapping(target = "emprestimos", ignore = true)
    Membro toMembro(MembroRequest membroRequest);

    @Mapping(source = "emprestimos", target = "idEmprestimos", qualifiedByName = "getListId")
    MembroResponse toResponse(Membro membro);

    @Named("getListId")
    default List<Long> getListId(List<Emprestimo> emprestimos) {
        if(emprestimos == null) return new ArrayList<>();
        return emprestimos.stream().map(Emprestimo::getId).toList();
    }
}
