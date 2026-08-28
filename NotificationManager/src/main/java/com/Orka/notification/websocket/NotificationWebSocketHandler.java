package com.Orka.notification.websocket;

import com.Orka.notification.config.WebSocketAuthHandshakeInterceptor;
import com.Orka.notification.model.NotificationRequest;
import com.Orka.notification.service.NotificationDispatchService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@Component
public class NotificationWebSocketHandler extends TextWebSocketHandler {
    private final NotificationSessionRegistry sessionRegistry;
    private final NotificationDispatchService dispatchService;
    private final ObjectMapper objectMapper;

    public NotificationWebSocketHandler(
            NotificationSessionRegistry sessionRegistry,
            NotificationDispatchService dispatchService,
            ObjectMapper objectMapper) {
        this.sessionRegistry = sessionRegistry;
        this.dispatchService = dispatchService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String username = username(session);
        if (username == null || username.isBlank()) {
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("missing authenticated username"));
            return;
        }

        sessionRegistry.register(username, session);
        sessionRegistry.send(session, dispatchService.connectedMessage(username));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        String username = username(session);
        if (username == null || username.isBlank()) {
            sessionRegistry.send(session, dispatchService.errorMessage("missing authenticated username"));
            return;
        }

        try {
            NotificationRequest request = objectMapper.readValue(message.getPayload(), NotificationRequest.class);
            dispatchService.dispatchClientRequest(username, request);
        } catch (Exception exception) {
            log.warn("Invalid websocket notification message from {}", username, exception);
            sessionRegistry.send(session, dispatchService.errorMessage(exception.getMessage()));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessionRegistry.unregister(session);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.warn("Notification websocket transport error for session {}", session.getId(), exception);
        sessionRegistry.unregister(session);
    }

    private String username(WebSocketSession session) {
        Object username = session.getAttributes().get(WebSocketAuthHandshakeInterceptor.USERNAME_ATTRIBUTE);
        return username instanceof String value ? value : null;
    }
}
