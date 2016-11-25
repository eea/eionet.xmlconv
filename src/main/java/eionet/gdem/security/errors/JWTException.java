package eionet.gdem.security.errors;

/**
 *
 * @author Vasilis Skiadas<vs@eworx.gr>
 */
public class JWTException extends  RuntimeException {

    public JWTException() {
    }

    public JWTException(String message) {
        super(message);
    }

    public JWTException(String message, Throwable cause) {
        super(message, cause);
    }

    public JWTException(Throwable cause) {
        super(cause);
    }
    
    
}
