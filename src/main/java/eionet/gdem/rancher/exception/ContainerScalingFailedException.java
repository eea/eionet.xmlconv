package eionet.gdem.rancher.exception;

public class ContainerScalingFailedException extends Exception {

    public ContainerScalingFailedException() {
    }

    public ContainerScalingFailedException(String message) {
        super(message);
    }

    public ContainerScalingFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ContainerScalingFailedException(Throwable cause) {
        super(cause);
    }

}
