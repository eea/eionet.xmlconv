package eionet.gdem.rancher.exception;

public class RancherApiTimoutException extends Exception {

    public RancherApiTimoutException() {
    }

    public RancherApiTimoutException(String message) {
        super(message);
    }

    public RancherApiTimoutException(String message, Throwable cause) {
        super(message, cause);
    }

    public RancherApiTimoutException(Throwable cause) {
        super(cause);
    }

}
