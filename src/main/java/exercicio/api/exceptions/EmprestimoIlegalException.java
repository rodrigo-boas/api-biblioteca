package exercicio.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class EmprestimoIlegalException extends RuntimeException {
    public EmprestimoIlegalException(String message) {
        super(message);
    }

    public EmprestimoIlegalException() {
        super("Emprestimo não pôde ser concluído");
    }
}
