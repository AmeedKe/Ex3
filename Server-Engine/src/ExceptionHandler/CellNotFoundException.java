package ExceptionHandler;

public class CellNotFoundException extends RuntimeException {
    public CellNotFoundException(String message) {
        super(message);
    }
}
