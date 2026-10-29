package exercicio.api.exceptions;

public class EmprestimoIlegalException extends RuntimeException {
    public EmprestimoIlegalException(String message) {
        super(message);
    }

    public EmprestimoIlegalException() {
        super("Emprestimo não pôde ser concluído");
    }



}
