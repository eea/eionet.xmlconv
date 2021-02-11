package eionet.gdem.rabbitMQ.errors;

public class CreateRabbitMQMessageException extends Exception {

    public CreateRabbitMQMessageException() {
    }

    public CreateRabbitMQMessageException(String message) {
        super(message);
    }

    public CreateRabbitMQMessageException(String message, Throwable cause) {
        super(message, cause);
    }

    public CreateRabbitMQMessageException(Throwable cause) {
        super(cause);
    }
}
