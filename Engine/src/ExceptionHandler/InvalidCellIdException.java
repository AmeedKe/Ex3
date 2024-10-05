package ExceptionHandler;

public class InvalidCellIdException extends RuntimeException {
    public InvalidCellIdException(String message) {
        super(message);
    }
}
