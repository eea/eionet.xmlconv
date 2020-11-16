package eionet.gdem.rabbitMq;

public class RabbitMQException extends  Exception{

    public RabbitMQException() {
    }

    public RabbitMQException(String message) {
        super(message);
    }

    public RabbitMQException(String message, Throwable cause) {
        super(message, cause);
    }

    public RabbitMQException(Throwable cause) {
        super(cause);
    }
}
