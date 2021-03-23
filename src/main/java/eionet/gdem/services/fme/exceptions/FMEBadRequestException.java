package eionet.gdem.services.fme.exceptions;

public class FMEBadRequestException extends Exception {

    public FMEBadRequestException() {
    }

    public FMEBadRequestException(String message) {
        super(message);
    }

    public FMEBadRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public FMEBadRequestException(Throwable cause) {
        super(cause);
    }

    public FMEBadRequestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
