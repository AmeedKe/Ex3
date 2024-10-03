package ExceptionHandler;

/**
 * Exception thrown when a range with the same name already exists.
 */
public class RangeExistsException extends Exception {
    public RangeExistsException(String message) {
        super(message);
    }
}