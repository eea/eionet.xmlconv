package eionet.gdem.http;

public class FollowRedirectException extends Exception {

    public FollowRedirectException() {
    }

    public FollowRedirectException(String message) {
        super(message);
    }

    public FollowRedirectException(String message, Throwable cause) {
        super(message, cause);
    }
}
