package eionet.gdem.exceptions;

/**
 *
 */
public class PathNotFoundException extends RuntimeException {

    public PathNotFoundException() {
    }

    public PathNotFoundException(String s) {
        super(s);
    }

    public PathNotFoundException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public PathNotFoundException(Throwable throwable) {
        super(throwable);
    }

    public PathNotFoundException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
