package eionet.gdem.api.errors;

/**
 *
 * @author Vasilis Skiadas<vs@eworx.gr>
 */
public class QaServiceException extends Exception {

    public QaServiceException(String message) {
        super(message);
    }
    public QaServiceException() {
    }
    public QaServiceException(String message, Throwable cause) {
        super(message, cause);
    }

}
