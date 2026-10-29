package exercicio.api.exceptions;

public class EmprestimoNotFoundException extends RuntimeException {
    public EmprestimoNotFoundException(String message) {
        super(message);
    }

    public EmprestimoNotFoundException() {
        super("Emprestimo não encontrado");
    }



}
