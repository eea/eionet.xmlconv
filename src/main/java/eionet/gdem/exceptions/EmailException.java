package eionet.gdem.exceptions;

public class EmailException extends Exception{

    /** Serial version UID. */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     */
    public EmailException() {
        super();
    }

    /**
     * Constructor.
     *
     * @param message
     */
    public EmailException(String message) {
        super(message);
    }

    /**
     * Constructor.
     *
     * @param message
     * @param cause
     */
    public EmailException(String message, Throwable cause) {
        super(message, cause);
    }
}