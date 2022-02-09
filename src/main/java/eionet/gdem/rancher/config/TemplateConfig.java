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
                .waitDurationInOpenState(Duration.ofMinutes(3))
                .minimumNumberOfCalls(3)     //αν αποτύχουν 3 requests, το circuit breaker θα ανοίξει
                .permittedNumberOfCallsInHalfOpenState(1)
                .build();
        CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(config);
        CircuitBreaker circuitBreaker = registry.circuitBreaker("rancherCircuitBreaker");
        circuitBreaker.getEventPublisher().onCallNotPermitted(event -> System.out.println("CALL NOT PERMITTED"));
        return circuitBreaker;
    }


}
