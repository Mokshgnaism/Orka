package com.Orka.notification.config;

import com.Orka.notification.websocket.NotificationWebSocketHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import java.util.Arrays;

@Configuration
@EnableWebSocket
public class NotificationWebSocketConfig implements WebSocketConfigurer {
    private final NotificationWebSocketHandler notificationWebSocketHandler;
    private final WebSocketAuthHandshakeInterceptor authHandshakeInterceptor;
    private final String allowedOriginPatterns;

    public NotificationWebSocketConfig(
            NotificationWebSocketHandler notificationWebSocketHandler,
            WebSocketAuthHandshakeInterceptor authHandshakeInterceptor,
            @Value("${notification.websocket.allowed-origin-patterns:*}") String allowedOriginPatterns) {
        this.notificationWebSocketHandler = notificationWebSocketHandler;
        this.authHandshakeInterceptor = authHandshakeInterceptor;
        this.allowedOriginPatterns = allowedOriginPatterns;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(notificationWebSocketHandler, "/ws/notifications")
                .addInterceptors(authHandshakeInterceptor)
                .setAllowedOriginPatterns(parseAllowedOriginPatterns());
    }

    private String[] parseAllowedOriginPatterns() {
        return Arrays.stream(allowedOriginPatterns.split(","))
                .map(String::trim)
                .filter(pattern -> !pattern.isBlank())
                .toArray(String[]::new);
    }
}
