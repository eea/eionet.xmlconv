package eionet.gdem.exceptions;

public class QuartzInitializationException extends Exception {

    public QuartzInitializationException() {
    }

    public QuartzInitializationException(String message) {
        super(message);
    }

    public QuartzInitializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
