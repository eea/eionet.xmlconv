package eionet.gdem.api.errors;

/**
 *
 * @author Vasilis Skiadas<vs@eworx.gr>
 */
public class BadRequestException extends Exception {

    public BadRequestException() {
    }

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public BadRequestException(Throwable cause) {
        super(cause);
    }

}
