package eionet.gdem.rancher.config;

import eionet.gdem.Properties;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.apache.commons.codec.binary.Base64;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
public class TemplateConfig {

    /**
     * time in seconds, corresponds to 2 minutes
     */
    private final int TIMEOUT = (int) TimeUnit.SECONDS.toMillis(120);
    
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
        circuitBreaker.getEventPublisher().onCallNotPermitted(event -> System.out.println(event));
        return circuitBreaker;
    }


}
