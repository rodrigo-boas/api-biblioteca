package exercicio.api.exceptions;

public class NullValueException extends RuntimeException {
    public NullValueException(String message) {
        super(message);
    }

    public NullValueException() {
        super("Objeto com atributo obrigatório vazio");
    }
}
