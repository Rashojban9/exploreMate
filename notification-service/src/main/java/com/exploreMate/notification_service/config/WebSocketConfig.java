package com.exploreMate.notification_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Enable a simple memory-based message broker to carry the greetings back to the client on destinations prefixed with "/queue"
        config.enableSimpleBroker("/queue");
        config.setApplicationDestinationPrefixes("/app");
        // Prefix used to address a specific user's queue
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // The endpoint the client uses to connect to the WebSocket server
        registry.addEndpoint("/ws/notifications")
                .setAllowedOriginPatterns("*")
                .withSockJS(); // Fallback option
                
        registry.addEndpoint("/ws/notifications")
                .setAllowedOriginPatterns("*"); // Raw WebSocket
    }
}
