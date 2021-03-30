package eionet.gdem.services.fme.exceptions;

public class FmeCommunicationException extends Exception {

    public FmeCommunicationException() {
    }

    public FmeCommunicationException(String message) {
        super(message);
    }

    public FmeCommunicationException(String message, Throwable cause) {
        super(message, cause);
    }

    public FmeCommunicationException(Throwable cause) {
        super(cause);
    }

    public FmeCommunicationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
