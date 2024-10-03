package ExceptionHandler;

/**
 * Exception thrown when a range is invalid.
 */
public class InvalidRangeException extends Exception {
    public InvalidRangeException(String message) {
        super(message);
    }
}
