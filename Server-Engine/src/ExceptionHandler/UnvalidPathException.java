package ExceptionHandler;

/**
 * Exception thrown when the provided file path is invalid.
 */
public class UnvalidPathException extends RuntimeException {
    public UnvalidPathException(String message) {
        super(message);
    }
}
