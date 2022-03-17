package eionet.gdem.web.sockets.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.handler.invocation.HandlerMethodReturnValueHandler;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;
import org.springframework.session.ExpiringSession;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.session.web.socket.config.annotation.AbstractSessionWebSocketMessageBrokerConfigurer;


import javax.mail.Session;
import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/websocket/workqueue");
        config.setApplicationDestinationPrefixes("/app");
    }

    /**
     * Create a websocket endpoint mapped to /restapi/workqueueData/getWorkqueuePageInfo
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        //registry.addEndpoint("/restapi/workqueueData/getWorkqueuePageInfo").withSockJS();
        //registry.addEndpoint("/new/workqueue").setAllowedOrigins("*").withSockJS();
        registry.addEndpoint("/websocket/workqueue/tableChanged").setAllowedOrigins("*").withSockJS();
    }

}