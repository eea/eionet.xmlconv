package eionet.gdem.rabbitMQ;

import eionet.gdem.Properties;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringRabbitMqConfig {

    public final static String WORKERS_JOBS_QUEUE = "workers-jobs-queue";

    public final static String WORKERS_JOBS_RESULTS_QUEUE= "workers-jobs-results-queue";

    public final static String MAIN_XMLCONV_JOBS_EXCHANGE = "main-xmlconv-jobs-exchange";

    public final static String MAIN_WORKERS_EXCHANGE = "main-workers-exchange";

    public final static String JOBS_ROUTING_KEY ="xmlconv-job";

    public final static String JOBS_RESULTS_ROUTING_KEY ="xmlconv-job-result";

    @Bean
    Queue workersJobsQueue() {
        return new Queue(WORKERS_JOBS_QUEUE, true);
    }


    @Bean
    Queue workersJobsResultsQueue() {
        return new Queue(WORKERS_JOBS_RESULTS_QUEUE, true);
    }

    @Bean
    DirectExchange mainXmlconvJobsExchange() {
        return new DirectExchange(MAIN_XMLCONV_JOBS_EXCHANGE,true,false);
    }


    @Bean
    DirectExchange mainWorkersExchange() {
        return new DirectExchange(MAIN_WORKERS_EXCHANGE,true,false);
    }

    @Bean
    Binding xmlconvExchangeToXmlConvJobsQUeueBinding() {
         return BindingBuilder.bind(workersJobsQueue()).to(mainXmlconvJobsExchange()).with("*.*");

    }

    @Bean
    Binding workersExchangeToWorkersJobResultsQueueBinding() {
        return BindingBuilder.bind(workersJobsResultsQueue()).to(mainWorkersExchange()).with("*.*");

    }

    @Bean
    SimpleMessageListenerContainer workersJobsResultsContainer(ConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(WORKERS_JOBS_RESULTS_QUEUE);
        container.setMessageListener(listenerAdapter);
        return container;
    }
    @Bean
    WorkersJobsResultsMessageReceiver workersJobsResultsMessageReceiver() {
        return new WorkersJobsResultsMessageReceiver();
    }
    @Bean
    MessageListenerAdapter workersJobsResultsListenerAdapter() {
        return new MessageListenerAdapter(workersJobsResultsMessageReceiver(), jsonMessageConverter());
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }


    @Bean
    ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(Properties.rabbitMQHost);
        connectionFactory.setPort(Properties.rabbitMQPort);
        connectionFactory.setUsername(Properties.rabbitMQUsername);
        connectionFactory.setPassword(Properties.rabbitMQPassword);
        return connectionFactory;
    }


    @Bean
    RabbitAdmin rabbitAdmin() {
        RabbitAdmin admin = new RabbitAdmin(connectionFactory());
        admin.setAutoStartup(true);

        admin.declareExchange(mainWorkersExchange());
        admin.declareExchange(mainXmlconvJobsExchange());

        admin.declareQueue(workersJobsQueue());
        admin.declareQueue(workersJobsResultsQueue());

        admin.declareBinding(workersExchangeToWorkersJobResultsQueueBinding());
        admin.declareBinding(xmlconvExchangeToXmlConvJobsQUeueBinding());
        return admin;
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        return new RabbitTemplate(connectionFactory());
    }
}
