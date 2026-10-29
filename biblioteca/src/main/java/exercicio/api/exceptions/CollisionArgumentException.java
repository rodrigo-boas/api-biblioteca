package exercicio.api.exceptions;

public class CollisionArgumentException extends RuntimeException {
    public CollisionArgumentException(String message) {
        super(message);
    }

    public CollisionArgumentException() {
        super("Argumento duplicado");
    }



}
