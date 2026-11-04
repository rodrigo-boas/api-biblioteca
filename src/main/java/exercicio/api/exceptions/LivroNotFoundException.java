package exercicio.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class LivroNotFoundException extends RuntimeException {
    public LivroNotFoundException(String message) {
        super(message);
    }

    public LivroNotFoundException() {
        super("Livro não encontrado");
    }
}
