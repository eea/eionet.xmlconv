package eionet.gdem.rabbitMQ;

import eionet.gdem.Properties;
import eionet.gdem.rabbitMQ.model.WorkersRabbitMQResponse;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Conditional(RabbitMqProdEnabledCondition.class)
public class SpringRabbitMqConfig {

    @Bean
    Queue workersJobsQueue() {
        return new Queue(Properties.WORKERS_JOBS_QUEUE, true);
    }


    @Bean
    Queue workersJobsResultsQueue() {
        return new Queue(Properties.WORKERS_JOBS_RESULTS_QUEUE, true);
    }

    @Bean
    DirectExchange mainXmlconvJobsExchange() {
        return new DirectExchange(Properties.MAIN_XMLCONV_JOBS_EXCHANGE,true,false);
    }


    @Bean
    DirectExchange mainWorkersExchange() {
        return new DirectExchange(Properties.MAIN_WORKERS_EXCHANGE,true,false);
    }

    @Bean
    Binding xmlconvExchangeToXmlConvJobsQUeueBinding() {
         return BindingBuilder.bind(workersJobsQueue()).to(mainXmlconvJobsExchange()).with(Properties.JOBS_ROUTING_KEY);

    }

    @Bean
    Binding workersExchangeToWorkersJobResultsQueueBinding() {
        return BindingBuilder.bind(workersJobsResultsQueue()).to(mainWorkersExchange()).with(Properties.JOBS_RESULTS_ROUTING_KEY);

    }

    @Bean
    Queue workersJobsOnDemandQueue() {
        return new Queue(Properties.WORKERS_JOBS_ON_DEMAND_QUEUE, true);
    }


    @Bean
    Queue workersJobsResultsOnDemandQueue() {
        return new Queue(Properties.WORKERS_JOBS_RESULTS_ON_DEMAND_QUEUE, true);
    }

    @Bean
    DirectExchange mainXmlconvJobsOnDemandExchange() {
        return new DirectExchange(Properties.MAIN_XMLCONV_JOBS_ON_DEMAND_EXCHANGE,true,false);
    }


    @Bean
    DirectExchange mainWorkersOnDemandExchange() {
        return new DirectExchange(Properties.MAIN_WORKERS_ON_DEMAND_EXCHANGE,true,false);
    }

    @Bean
    Binding xmlconvExchangeToXmlConvJobsOnDemandQueueBinding() {
        return BindingBuilder.bind(workersJobsOnDemandQueue()).to(mainXmlconvJobsOnDemandExchange()).with(Properties.JOBS_ON_DEMAND_ROUTING_KEY);

    }

    @Bean
    Binding workersExchangeToWorkersJobResultsOnDemandQueueBinding() {
        return BindingBuilder.bind(workersJobsResultsOnDemandQueue()).to(mainWorkersOnDemandExchange()).with(Properties.JOBS_RESULTS_ON_DEMAND_ROUTING_KEY);

    }

    @Bean
    SimpleMessageListenerContainer workersJobsResultsContainer(ConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.addQueueNames(Properties.WORKERS_JOBS_RESULTS_QUEUE, Properties.WORKERS_JOBS_RESULTS_ON_DEMAND_QUEUE);
        container.setConnectionFactory(connectionFactory);
        container.setMessageListener(workersJobsResultsListenerAdapter());
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

        admin.declareExchange(mainWorkersOnDemandExchange());
        admin.declareExchange(mainXmlconvJobsOnDemandExchange());

        admin.declareQueue(workersJobsOnDemandQueue());
        admin.declareQueue(workersJobsResultsOnDemandQueue());

        admin.declareBinding(workersExchangeToWorkersJobResultsOnDemandQueueBinding());
        admin.declareBinding(xmlconvExchangeToXmlConvJobsOnDemandQueueBinding());
        return admin;
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory());
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}
