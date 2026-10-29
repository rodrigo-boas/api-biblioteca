package exercicio.api.mapper;

import exercicio.api.dto.EmprestimoRequest;
import exercicio.api.dto.EmprestimoResponse;
import exercicio.api.model.Emprestimo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.time.LocalDate;

@Mapper(componentModel = "spring", imports = LocalDate.class, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface EmprestimoMapper {

    @Mapping(target = "status", constant = "EMPRESTADO")
    @Mapping(target = "livro", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "membro", ignore = true)
    @Mapping(target = "dataEmprestimo", expression = "java(LocalDate.now())")
    @Mapping(target = "dataDevolucao", ignore = true)
    Emprestimo toEmprestimo(EmprestimoRequest emprestimoRequest);

    @Mapping(source = "livro.id", target = "idLivro")
    @Mapping(source = "membro.id", target = "idMembro")
    EmprestimoResponse toResponse(Emprestimo emprestimo);

}
