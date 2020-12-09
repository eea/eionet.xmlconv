package eionet.gdem.rabbitMQ.errors;

public class CreateMQMessageException extends Exception {

    public CreateMQMessageException() {
    }

    public CreateMQMessageException(String message) {
        super(message);
    }

    public CreateMQMessageException(String message, Throwable cause) {
        super(message, cause);
    }

    public CreateMQMessageException(Throwable cause) {
        super(cause);
    }
}
