package eionet.gdem.rabbitMq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import eionet.gdem.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RabbitMQConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQConsumer.class);
    private String QUEUE_NAME = null;

    private ConnectionFactory setupConnectionFactory(){
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(Properties.rabbitMQHost);
        factory.setPort(Properties.rabbitMQPort);
        factory.setUsername(Properties.rabbitMQUsername);
        factory.setPassword(Properties.rabbitMQPassword);
        return factory;
    }

    public void receiveMessageFromQueue() throws RabbitMQException {
        this.setQUEUE_NAME(Properties.rabbitMQQueueName);
        ConnectionFactory factory = setupConnectionFactory();

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            LOGGER.info("Waiting for messages from queue "  + QUEUE_NAME);

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                //The actual message that was received from the queue
                String message = new String(delivery.getBody(), "UTF-8");
                LOGGER.info("Received message from queue "  + QUEUE_NAME);
            };
            String message = channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {
            });
        }
        catch(Exception e){
            LOGGER.error("Error when receiving message to queue" + QUEUE_NAME);
            throw new RabbitMQException(e.getMessage());
        }
    }

    public String getQUEUE_NAME() {
        return QUEUE_NAME;
    }

    protected void setQUEUE_NAME(String QUEUE_NAME) {
        this.QUEUE_NAME = QUEUE_NAME;
    }
}
