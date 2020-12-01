package expression.exceptions;

public class DBZException extends RuntimeException {
    public DBZException() {
        super("division by zero");
    }
}
