package eionet.gdem.api.errors;

import java.io.IOException;

/**
 *
 */
public class BaseXJobInterruptedException extends IOException {

    public BaseXJobInterruptedException() {
    }

    public BaseXJobInterruptedException(String message) {
        super(message);
    }

    public BaseXJobInterruptedException(String message, Throwable cause) {
        super(message, cause);
    }

}
