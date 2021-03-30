package eionet.gdem.services.fme.exceptions;

public class GenericFMEexception extends Exception {
    public GenericFMEexception() {
    }

    public GenericFMEexception(String message) {
        super(message);
    }


    public GenericFMEexception(Throwable cause) {
        super(cause);
    }

    public GenericFMEexception(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
