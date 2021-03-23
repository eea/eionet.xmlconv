package eionet.gdem.services.fme.exceptions;

public class HttpRequestHeaderInitializationException extends Exception {
    public HttpRequestHeaderInitializationException() {
    }

    public HttpRequestHeaderInitializationException(String message) {
        super(message);
    }

    public HttpRequestHeaderInitializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpRequestHeaderInitializationException(Throwable cause) {
        super(cause);
    }
}
