package exercicio.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class EmprestimoNotFoundException extends RuntimeException {
    public EmprestimoNotFoundException(String message) {
        super(message);
    }

    public EmprestimoNotFoundException() {
        super("Emprestimo não encontrado");
    }
}
