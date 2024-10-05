package ExceptionHandler;

public class RangeInUseException extends RuntimeException {
    public RangeInUseException(String message) {
        super(message);
    }
}
