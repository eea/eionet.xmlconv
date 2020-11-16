package eionet.gdem.rabbitMq;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import eionet.gdem.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RabbitMQProducer {
    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQProducer.class);
    private String QUEUE_NAME = null;

    public RabbitMQProducer() {
    }

    protected ConnectionFactory setupConnectionFactory(){
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(Properties.rabbitMQHost);
        factory.setPort(Properties.rabbitMQPort);
        factory.setUsername(Properties.rabbitMQUsername);
        factory.setPassword(Properties.rabbitMQPassword);
        return factory;
    }

    public void sendMessageToQueue(String message) throws RabbitMQException {
        this.setQUEUE_NAME(Properties.rabbitMQQueueName);
        ConnectionFactory factory = setupConnectionFactory();

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.queueDeclare(this.getQUEUE_NAME(), false, false, false, null);
            channel.basicPublish("", this.getQUEUE_NAME(), null, message.getBytes());
            LOGGER.info("Message was sent to queue "  + this.getQUEUE_NAME());
        }
        catch(Exception e){
            LOGGER.error("Error when sending message to queue" + this.getQUEUE_NAME());
            throw new RabbitMQException(e.getMessage());
        }
    }

    protected String getQUEUE_NAME() {
        return QUEUE_NAME;
    }

    protected void setQUEUE_NAME(String QUEUE_NAME) {
        this.QUEUE_NAME = QUEUE_NAME;
    }
}
