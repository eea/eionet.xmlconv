package eionet.gdem.rancher.exception;

public class RancherApiException extends Exception {

    public RancherApiException() {
    }

    public RancherApiException(String message) {
        super(message);
    }

    public RancherApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public RancherApiException(Throwable cause) {
        super(cause);
    }

}
