package eionet.gdem.services.fme.exceptions;

import eionet.gdem.services.fme.FmeJobStatus;

public class FmeAuthorizationException extends Exception {
    public FmeAuthorizationException() {
    }

    public FmeAuthorizationException(String message) {
        super(message);
    }

    public FmeAuthorizationException(String message, Throwable cause) {
        super(message, cause);
    }

    public FmeAuthorizationException(Throwable cause) {
        super(cause);
    }

}

