package ExceptionHandler;

/**
 * Exception thrown when a range is not found.
 */
public class RangeNotFoundException extends Exception {
    public RangeNotFoundException(String message) {
        super(message);
    }
}