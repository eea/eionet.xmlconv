package eionet.gdem.rancher.config;

import eionet.gdem.Properties;
import eionet.gdem.jpa.Entities.AlertEntry;
import eionet.gdem.jpa.enums.AlertSeverity;
import eionet.gdem.jpa.service.AlertService;
import eionet.gdem.notifications.UNSEventSender;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.circuitbreaker.event.CircuitBreakerEvent;
import io.github.resilience4j.consumer.CircularEventConsumer;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.time.Duration;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Configuration
public class RestTemplateAndCircuitBreakerAndTaskSchedulerConfig {

    /**
     * time in seconds, corresponds to 2 minutes
     */
    private final int TIMEOUT = (int) TimeUnit.SECONDS.toMillis(120);

    private static final Logger LOGGER = LoggerFactory.getLogger(RestTemplateAndCircuitBreakerAndTaskSchedulerConfig.class);

    @Autowired
    AlertService alertService;

    @Bean
    public RestTemplate restTemplate() {
        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setConnectTimeout(TIMEOUT);
        httpRequestFactory.setReadTimeout(TIMEOUT);
        return new RestTemplate(httpRequestFactory);
    }

    public static HttpHeaders getHeaders() {
        String credentials = Properties.rancherApiAccessKey + ":" + Properties.rancherApiSecretKey;
        String encodedCredentials =
                new String(Base64.encodeBase64(credentials.getBytes()));

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Basic " + encodedCredentials);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return httpHeaders;
    }

    @Bean
    CircularEventConsumer circularEventConsumer() {
        CircularEventConsumer<CircuitBreakerEvent> circularEventConsumer =
                new CircularEventConsumer<>(10);
        return circularEventConsumer;
    }

    @Bean
    public CircuitBreaker circuitBreaker() {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .minimumNumberOfCalls(1)     //if 1 call fails in the last 60 seconds, circuit breaker will open
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.TIME_BASED)
                .slidingWindowSize(60)
                .waitDurationInOpenState(Duration.ofMinutes(3)) //circuit breaker will remain in open state for 3 minutes and no calls will be permitted
                .permittedNumberOfCallsInHalfOpenState(1)  //after 3 minutes in open state, circuit breaker gets half open state. In half open state only 1 call is permitted and if the call fails, circuit breaker opens again. If the call succeeds, circuit breaker gets closed state
                .build();
        CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(config);
        CircuitBreaker circuitBreaker = registry.circuitBreaker("rancherCircuitBreaker");
        circuitBreaker.getEventPublisher().onCallNotPermitted(circularEventConsumer());
        return circuitBreaker;
    }

    @Bean
    public TaskScheduler taskScheduler() {

        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(1);
        taskScheduler.initialize();
        taskScheduler.setErrorHandler(throwable -> {
            LOGGER.info("Caught exception in TaskScheduler. " + throwable.getMessage());
            //create alert and send it to uns
            Timestamp dateOccurred = new Timestamp(new Date().getTime());
            AlertEntry alertEntry = new AlertEntry().setSeverity(AlertSeverity.CRITICAL).setDescription("Scheduled task failed.").setOccurrenceDate(dateOccurred);
            try {
                new UNSEventSender().alertsNotification(dateOccurred.getTime() + ", Scheduled task failed", Properties.ALERTS_EVENT);
                alertEntry.setNotificationSentToUns(true);
                LOGGER.info("Sent scheduled task failure alert to UNS.");
            } catch (Exception e) {
                LOGGER.error("Could not send scheduled task failure alert to UNS. Exception is: " + e.getMessage());
                alertEntry.setNotificationSentToUns(false);
            }
            alertService.save(alertEntry);
        });
        return taskScheduler;
    }


}
