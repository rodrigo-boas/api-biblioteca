package exercicio.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class CollisionArgumentException extends RuntimeException {
    public CollisionArgumentException(String message) {
        super(message);
    }

    public CollisionArgumentException() {
        super("Argumento duplicado");
    }
}
