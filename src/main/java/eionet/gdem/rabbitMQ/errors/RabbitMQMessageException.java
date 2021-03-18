package eionet.gdem.rabbitMQ.errors;

public class RabbitMQMessageException extends Exception {

    public RabbitMQMessageException() {
    }

    public RabbitMQMessageException(String message) {
        super(message);
    }

    public RabbitMQMessageException(String message, Throwable cause) {
        super(message, cause);
    }

    public RabbitMQMessageException(Throwable cause) {
        super(cause);
    }
}
