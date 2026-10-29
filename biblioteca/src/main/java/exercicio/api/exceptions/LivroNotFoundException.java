package exercicio.api.exceptions;

public class LivroNotFoundException extends RuntimeException {
    public LivroNotFoundException(String message) {
        super(message);
    }

    public LivroNotFoundException() {
        super("Livro não encontrado");
    }



}
