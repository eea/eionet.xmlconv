package eionet.gdem.rabbitMQ;

import eionet.gdem.Properties;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
@Conditional(RabbitMqProdEnabledCondition.class)
public class SpringRabbitMqConfig {

    //Queue where converters sends script messages for workers to retrieve
    @Bean
    Queue workersJobsQueue() {
        return new Queue(Properties.WORKERS_JOBS_QUEUE, true);
    }

    //Queue where workers respond with results after executing a script
    @Bean
    Queue workersJobsResultsQueue() {
        return new Queue(Properties.WORKERS_JOBS_RESULTS_QUEUE, true);
    }

    //Queue where workers send their status
    @Bean
    Queue workersStatusQueue() {
        return new Queue(Properties.WORKERS_STATUS_QUEUE, true);
    }

    //Queue where converters sends message asking worker if it's executing a specific job
    @Bean
    Queue workerJobExecutionRequestQueue() {
        return new Queue(Properties.WORKER_JOB_EXECUTION_REQUEST_QUEUE, true);
    }

    //Queue where worker respond whether it's executing a specific job
    @Bean
    Queue workerJobExecutionResponseQueue() {
        return new Queue(Properties.WORKER_JOB_EXECUTION_RESPONSE_QUEUE, true);
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
    Binding workersExchangeToWorkersStatusQueueBinding() {
        return BindingBuilder.bind(workersStatusQueue()).to(mainWorkersExchange()).with(Properties.WORKER_STATUS_ROUTING_KEY);
    }

    @Bean
    Binding exchangeToWorkerJobExecutionRequestQueueBinding() {
        return BindingBuilder.bind(workerJobExecutionRequestQueue()).to(mainXmlconvJobsExchange()).with(Properties.WORKER_JOB_EXECUTION_REQUEST_ROUTING_KEY);
    }

    @Bean
    Binding exchangeToWorkerJobExecutionResponseQueueBinding() {
        return BindingBuilder.bind(workerJobExecutionResponseQueue()).to(mainWorkersExchange()).with(Properties.WORKER_JOB_EXECUTION_RESPONSE_ROUTING_KEY);
    }

    @Bean
    SimpleMessageListenerContainer workersJobsResultsContainer(ConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(Properties.WORKERS_JOBS_RESULTS_QUEUE);
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
    SimpleMessageListenerContainer workersStatusContainer(ConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(Properties.WORKERS_STATUS_QUEUE);
        container.setMessageListener(workersStatusListenerAdapter());
        return container;
    }
    @Bean
    WorkersStatusMessageReceiver workersStatusMessageReceiver() {
        return new WorkersStatusMessageReceiver();
    }
    @Bean
    MessageListenerAdapter workersStatusListenerAdapter() {
        return new MessageListenerAdapter(workersStatusMessageReceiver(), jsonMessageConverter());
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
        admin.declareQueue(workersStatusQueue());
        admin.declareQueue(workerJobExecutionRequestQueue());
        admin.declareQueue(workerJobExecutionResponseQueue());

        admin.declareBinding(workersExchangeToWorkersJobResultsQueueBinding());
        admin.declareBinding(xmlconvExchangeToXmlConvJobsQUeueBinding());
        admin.declareBinding(workersExchangeToWorkersStatusQueueBinding());
        admin.declareBinding(exchangeToWorkerJobExecutionRequestQueueBinding());
        admin.declareBinding(exchangeToWorkerJobExecutionResponseQueueBinding());
        return admin;
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory());
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}
