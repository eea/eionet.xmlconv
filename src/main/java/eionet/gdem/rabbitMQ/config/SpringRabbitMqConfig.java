package eionet.gdem.rabbitMQ.config;

import eionet.gdem.Properties;
import eionet.gdem.rabbitMQ.listeners.*;
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
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@Conditional(RabbitMqProdEnabledCondition.class)
public class SpringRabbitMqConfig {

    private static final Integer X_MAX_PRIORITY = 5;

    //Queue where converters sends script messages for workers to retrieve. The queue is set as priority queue, so that
    //onDemand jobs can be marked with higher priority and take precedence over other jobs.
    @Bean
    Queue workersJobsQueue() {
        return QueueBuilder.durable(Properties.WORKERS_JOBS_QUEUE)
                .withArgument("x-dead-letter-exchange", Properties.WORKERS_DEAD_LETTER_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", Properties.WORKERS_DEAD_LETTER_ROUTING_KEY)
                .withArgument("x-max-priority", X_MAX_PRIORITY)
                .build();
    }

    //Queue where converters sends script messages for heavy jobs for heavy workers to retrieve. The queue is set as priority queue, so that
    //onDemand jobs can be marked with higher priority and take precedence over other jobs.
    @Bean
    Queue heavyWorkersJobsQueue() {
        return QueueBuilder.durable(Properties.HEAVY_WORKERS_JOBS_QUEUE)
                .withArgument("x-dead-letter-exchange", Properties.WORKERS_DEAD_LETTER_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", Properties.WORKERS_DEAD_LETTER_ROUTING_KEY)
                .withArgument("x-max-priority", X_MAX_PRIORITY)
                .build();
    }

    //Queue where converters sends script messages for synchronous fme jobs for workers to retrieve. The queue is set as priority queue, so that
    //onDemand jobs can be marked with higher priority and take precedence over other jobs.
    @Bean
    Queue syncFmeWorkersJobsQueue() {
        return QueueBuilder.durable(Properties.SYNC_FME_JOBS_QUEUE)
                .withArgument("x-dead-letter-exchange", Properties.WORKERS_DEAD_LETTER_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", Properties.WORKERS_DEAD_LETTER_ROUTING_KEY)
                .withArgument("x-max-priority", X_MAX_PRIORITY)
                .build();
    }

    //Queue where converters sends script messages for asynchronous fme jobs for workers to retrieve. The queue is set as priority queue, so that
    //onDemand jobs can be marked with higher priority and take precedence over other jobs.
    @Bean
    Queue asyncFmeWorkersJobsQueue() {
        return QueueBuilder.durable(Properties.ASYNC_FME_JOBS_QUEUE)
                .withArgument("x-dead-letter-exchange", Properties.WORKERS_DEAD_LETTER_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", Properties.WORKERS_DEAD_LETTER_ROUTING_KEY)
                .withArgument("x-max-priority", X_MAX_PRIORITY)
                .build();
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

    //Queue where worker respond whether it's executing a specific job
    @Bean
    Queue workerHeartBeatResponseQueue() {
        return new Queue(Properties.WORKER_HEART_BEAT_RESPONSE_QUEUE, true);
    }


    //Queue where rejected messages go to
    @Bean
    Queue deadLetterQueue() {
        return new Queue(Properties.WORKERS_DEAD_LETTER_QUEUE, true);
    }

    @Bean
    Queue xmlconvHealthQueue() {
        return new Queue(Properties.XMLCONV_HEALTH_QUEUE, true);
    }

    //Queue where converters listens for cdr requests
    @Bean
    Queue cdrJobsRequestQueue() {
        return QueueBuilder.durable(Properties.CDR_REQUEST_QUEUE)
                .withArgument("x-dead-letter-exchange", Properties.CDR_DEAD_LETTER_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", Properties.CDR_DEAD_LETTER_ROUTING_KEY)
                .withArgument("x-message-ttl", Properties.CDR_REQUEST_QUEUE_TTL)
                .build();
    }

    //Queue where converters sends results for cdr to retrieve
    @Bean
    Queue cdrJobsResultsQueue() {
        return new Queue(Properties.CDR_RESULTS_QUEUE, true);
    }

    //Queue where cdr rejected requests go to
    @Bean
    Queue cdrDeadLetterQueue() {
        return new Queue(Properties.CDR_DEAD_LETTER_QUEUE, true);
    }

    //Exchange where converters sends message asking worker if it's executing a specific job
    @Bean
    FanoutExchange workersHeartBeatRequestExchange() {
        return new FanoutExchange(Properties.XMLCONV_HEART_BEAT_REQUEST_EXCHANGE,true,false);
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
    DirectExchange mainXmlconvHeavyWorkersExchange() {
        return new DirectExchange(Properties.MAIN_XMLCONV_HEAVY_JOBS_EXCHANGE,true,false);
    }

    @Bean
    DirectExchange xmlconvSyncFmeWorkersExchange() {
        return new DirectExchange(Properties.XMLCONV_SYNC_FME_JOBS_EXCHANGE,true,false);
    }

    @Bean
    DirectExchange xmlconvAsyncFmeWorkersExchange() {
        return new DirectExchange(Properties.XMLCONV_ASYNC_FME_JOBS_EXCHANGE,true,false);
    }

    @Bean
    DirectExchange deadLetterExchange() {
        return new DirectExchange(Properties.WORKERS_DEAD_LETTER_EXCHANGE);
    }

    @Bean
    DirectExchange xmlconvHealthExchange() {
        return new DirectExchange(Properties.XMLCONV_HEALTH_EXCHANGE,true,false);
    }

    @Bean
    DirectExchange cdrJobsRequestExchange() {
        return new DirectExchange(Properties.CDR_REQUEST_EXCHANGE,true,false);
    }

    @Bean
    DirectExchange cdrJobsResultsExchange() {
        return new DirectExchange(Properties.CDR_RESULTS_EXCHANGE,true,false);
    }

    @Bean
    DirectExchange cdrDeadLetterExchange() {
        return new DirectExchange(Properties.CDR_DEAD_LETTER_EXCHANGE,true,false);
    }

    @Bean
    Binding xmlconvExchangeToXmlConvJobsQueueBinding() {
        return BindingBuilder.bind(workersJobsQueue()).to(mainXmlconvJobsExchange()).with(Properties.JOBS_ROUTING_KEY);
    }

    @Bean
    Binding xmlconvExchangeToXmlConvHeavyJobsQueueBinding() {
        return BindingBuilder.bind(heavyWorkersJobsQueue()).to(mainXmlconvHeavyWorkersExchange()).with(Properties.HEAVY_JOBS_ROUTING_KEY);
    }

    @Bean
    Binding exchangeToSyncFmeJobsQueueBinding() {
        return BindingBuilder.bind(syncFmeWorkersJobsQueue()).to(xmlconvSyncFmeWorkersExchange()).with(Properties.SYNC_FME_JOBS_ROUTING_KEY);
    }

    @Bean
    Binding exchangeToAsyncFmeJobsQueueBinding() {
        return BindingBuilder.bind(asyncFmeWorkersJobsQueue()).to(xmlconvAsyncFmeWorkersExchange()).with(Properties.ASYNC_FME_JOBS_ROUTING_KEY);
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
    Binding exchangeToWorkerHeartBeatResponseQueueBinding() {
        return BindingBuilder.bind(workerHeartBeatResponseQueue()).to(mainWorkersExchange()).with(Properties.WORKER_HEART_BEAT_RESPONSE_ROUTING_KEY);
    }

    @Bean
    Binding exchangeToDeadLetterQueueBinding() {
        return BindingBuilder.bind(deadLetterQueue()).to(deadLetterExchange()).with(Properties.WORKERS_DEAD_LETTER_ROUTING_KEY);
    }

    @Bean
    Binding exchangeToXmlconvHealthQueueBinding() {
        return BindingBuilder.bind(xmlconvHealthQueue()).to(xmlconvHealthExchange()).with(Properties.XMLCONV_HEALTH_ROUTING_KEY);
    }

    @Bean
    Binding exchangeToCdrRequestQueueBinding() {
        return BindingBuilder.bind(cdrJobsRequestQueue()).to(cdrJobsRequestExchange()).with(Properties.CDR_REQUEST_ROUTING_KEY);
    }

    @Bean
    Binding exchangeToCdrResultsQueueBinding() {
        return BindingBuilder.bind(cdrJobsResultsQueue()).to(cdrJobsResultsExchange()).with(Properties.CDR_RESULTS_ROUTING_KEY);
    }

    @Bean
    Binding exchangeToCdrDeadLetterQueueBinding() {
        return BindingBuilder.bind(cdrDeadLetterQueue()).to(cdrDeadLetterExchange()).with(Properties.CDR_DEAD_LETTER_ROUTING_KEY);
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
    SimpleMessageListenerContainer workerHeartBeatResponseContainer(ConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(Properties.WORKER_HEART_BEAT_RESPONSE_QUEUE);
        container.setMessageListener(workerHeartBeatResponseListenerAdapter());
        return container;
    }
    @Bean
    WorkerHeartBeatResponseReceiver workerHeartBeatResponseReceiver() {
        return new WorkerHeartBeatResponseReceiver();
    }
    @Bean
    MessageListenerAdapter workerHeartBeatResponseListenerAdapter() {
        return new MessageListenerAdapter(workerHeartBeatResponseReceiver(), jsonMessageConverter());
    }

    @Bean
    DeadLetterQueueMessageReceiver deadLetterQueueMessageReceiver() {
        return new DeadLetterQueueMessageReceiver();
    }
    @Bean
    MessageListenerAdapter deadLetterQueueListenerAdapter() {
        return new MessageListenerAdapter(deadLetterQueueMessageReceiver(), jsonMessageConverter());
    }
    @Bean
    SimpleMessageListenerContainer deadLetterQueueMessagesContainer(ConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(Properties.WORKERS_DEAD_LETTER_QUEUE);
        container.setMessageListener(deadLetterQueueListenerAdapter());
        return container;
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Primary
    @Bean
    ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(Properties.rabbitMQHost);
        connectionFactory.setPort(Properties.rabbitMQPort);
        connectionFactory.setUsername(Properties.rabbitMQUsername);
        connectionFactory.setPassword(Properties.rabbitMQPassword);
        return connectionFactory;
    }

    @Bean
    ConnectionFactory cdrConnectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(Properties.cdrRabbitMQHost);
        connectionFactory.setPort(Properties.cdrRabbitMQPort);
        connectionFactory.setUsername(Properties.cdrRabbitMQUsername);
        connectionFactory.setPassword(Properties.cdrRabbitMQPassword);
        return connectionFactory;
    }

    @Bean
    SimpleMessageListenerContainer cdrRequestQueueContainer() {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(cdrConnectionFactory());
        container.setQueueNames(Properties.CDR_REQUEST_QUEUE);
        container.setMessageListener(cdrRequestQueueListenerAdapter());
        return container;
    }
    @Bean
    CdrRequestMessageReceiver cdrRequestMessageReceiver() {
        return new CdrRequestMessageReceiver();
    }
    @Bean
    MessageListenerAdapter cdrRequestQueueListenerAdapter() {
        return new MessageListenerAdapter(cdrRequestMessageReceiver(), jsonMessageConverter());
    }

    @Primary
    @Bean
    RabbitAdmin rabbitAdmin() {
        RabbitAdmin admin = new RabbitAdmin(connectionFactory());
        admin.setAutoStartup(true);
        admin.setExplicitDeclarationsOnly(true);

        admin.declareExchange(mainWorkersExchange());
        admin.declareExchange(mainXmlconvJobsExchange());
        admin.declareExchange(mainXmlconvHeavyWorkersExchange());
        admin.declareExchange(workersHeartBeatRequestExchange());
        admin.declareExchange(deadLetterExchange());
        admin.declareExchange(xmlconvSyncFmeWorkersExchange());
        admin.declareExchange(xmlconvAsyncFmeWorkersExchange());
        admin.declareExchange(xmlconvHealthExchange());

        admin.declareQueue(workersJobsQueue());
        admin.declareQueue(heavyWorkersJobsQueue());
        admin.declareQueue(workersJobsResultsQueue());
        admin.declareQueue(workersStatusQueue());
        admin.declareQueue(workerHeartBeatResponseQueue());
        admin.declareQueue(deadLetterQueue());
        admin.declareQueue(syncFmeWorkersJobsQueue());
        admin.declareQueue(asyncFmeWorkersJobsQueue());
        admin.declareQueue(xmlconvHealthQueue());

        admin.declareBinding(workersExchangeToWorkersJobResultsQueueBinding());
        admin.declareBinding(xmlconvExchangeToXmlConvJobsQueueBinding());
        admin.declareBinding(xmlconvExchangeToXmlConvHeavyJobsQueueBinding());
        admin.declareBinding(workersExchangeToWorkersStatusQueueBinding());
        admin.declareBinding(exchangeToWorkerHeartBeatResponseQueueBinding());
        admin.declareBinding(exchangeToDeadLetterQueueBinding());
        admin.declareBinding(exchangeToSyncFmeJobsQueueBinding());
        admin.declareBinding(exchangeToAsyncFmeJobsQueueBinding());
        admin.declareBinding(exchangeToXmlconvHealthQueueBinding());

        return admin;
    }

    @Bean
    RabbitAdmin cdrRabbitAdmin() {
        RabbitAdmin cdrRabbitAdmin = new RabbitAdmin(cdrConnectionFactory());
        cdrRabbitAdmin.setAutoStartup(true);
        cdrRabbitAdmin.setExplicitDeclarationsOnly(true);

        cdrRabbitAdmin.declareQueue(cdrJobsRequestQueue());
        cdrRabbitAdmin.declareQueue(cdrJobsResultsQueue());
        cdrRabbitAdmin.declareQueue(cdrDeadLetterQueue());

        cdrRabbitAdmin.declareExchange(cdrJobsRequestExchange());
        cdrRabbitAdmin.declareExchange(cdrJobsResultsExchange());
        cdrRabbitAdmin.declareExchange(cdrDeadLetterExchange());

        cdrRabbitAdmin.declareBinding(exchangeToCdrRequestQueueBinding());
        cdrRabbitAdmin.declareBinding(exchangeToCdrResultsQueueBinding());
        cdrRabbitAdmin.declareBinding(exchangeToCdrDeadLetterQueueBinding());
        return cdrRabbitAdmin;
    }

    @Primary
    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory());
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public RabbitTemplate cdrRabbitTemplate() {
        RabbitTemplate cdrRabbitTemplate = new RabbitTemplate(cdrConnectionFactory());
        cdrRabbitTemplate.setMessageConverter(jsonMessageConverter());
        return cdrRabbitTemplate;
    }

}
