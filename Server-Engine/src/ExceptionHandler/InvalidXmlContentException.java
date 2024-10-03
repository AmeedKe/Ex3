package ExceptionHandler;

public class InvalidXmlContentException extends RuntimeException {
    public InvalidXmlContentException(String message) {
        super(message);
    }
}
