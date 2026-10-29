package exercicio.api.exceptions;

public class MembroNotFoundException extends RuntimeException {
    public MembroNotFoundException(String message) {
        super(message);
    }

    public MembroNotFoundException() {
        super("Membro não encontrado");
    }



}
