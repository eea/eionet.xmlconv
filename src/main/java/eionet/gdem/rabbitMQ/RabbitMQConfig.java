//package eionet.gdem.rabbitMQ;
//
//import com.rabbitmq.client.Channel;
//import com.rabbitmq.client.Connection;
//import com.rabbitmq.client.ConnectionFactory;
//import eionet.gdem.Properties;
//import eionet.gdem.services.RabbitMQConsumerService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import javax.annotation.PostConstruct;
//import javax.annotation.Resource;
//import java.io.IOException;
//import java.util.concurrent.TimeoutException;
//
////@Configuration
//public class RabbitMQConfig {
//
//    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQConfig.class);
//
//   // @PostConstruct
//  //  @Bean("connectionFactory")
//    public ConnectionFactory connectionFactory() {
//        ConnectionFactory factory = new ConnectionFactory();
//        factory.setHost(Properties.rabbitMQHost);
//        factory.setPort(Properties.rabbitMQPort);
//        factory.setUsername(Properties.rabbitMQUsername);
//        factory.setPassword(Properties.rabbitMQPassword);
//        return factory;
//    }
//
//  //  @Autowired
//   // @Resource(name = "connectionFactory")
//    private ConnectionFactory connectionFactory;
//
//  //  @PostConstruct
//  //  @Bean("mainChannel")
//    public Channel producerChannel(){
//        Channel channel = null;
//        try {
//            Connection connection = connectionFactory.newConnection();
//            channel = connection.createChannel();
//            channel.queueDeclare(Properties.rabbitMQProducerQueueName, false, true, false, null);
//        }
//        catch(Exception e){
//            LOGGER.error("Error when creating producer channel " + e.getMessage());
//        }
//        return channel;
//    }
//
// //   @PostConstruct
//  //  @Bean("consumerChannel")
//    public Channel consumerChannel(){
//        Channel channel = null;
//        try {
//            Connection connection = connectionFactory().newConnection();
//            channel = connection.createChannel();
//            channel.queueDeclare(Properties.rabbitMQConsumerQueueName, false, false, false, null);
//        }
//        catch(Exception e){
//            LOGGER.error("Error when creating consumer channel " + e.getMessage());
//        }
//        return channel;
//    }
//}
