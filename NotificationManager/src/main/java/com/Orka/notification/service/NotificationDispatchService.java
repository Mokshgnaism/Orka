package com.Orka.notification.service;

import com.Orka.notification.model.NotificationEnvelope;
import com.Orka.notification.model.NotificationRequest;
import com.Orka.notification.model.NotificationScope;
import com.Orka.notification.model.NotificationSeverity;
import com.Orka.notification.websocket.NotificationSessionRegistry;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
public class NotificationDispatchService {
    private final RecipientResolver recipientResolver;
    private final NotificationSessionRegistry sessionRegistry;
    private final ObjectMapper objectMapper;

    public NotificationDispatchService(
            RecipientResolver recipientResolver,
            NotificationSessionRegistry sessionRegistry,
            ObjectMapper objectMapper) {
        this.recipientResolver = recipientResolver;
        this.sessionRegistry = sessionRegistry;
        this.objectMapper = objectMapper;
    }

    public void dispatchClientRequest(String actorUsername, NotificationRequest request) {
        NotificationScope scope = NotificationScope.from(request.getScope());
        UUID resourceId = parseResourceId(scope, request.getResourceId());

        if (!recipientResolver.userCanPublish(actorUsername, scope, resourceId, request.getRecipientUsername())) {
            sendToUser(actorUsername, errorMessage("not authorized to publish notification for this resource"));
            return;
        }

        Set<String> recipients = recipientResolver.resolve(scope, resourceId, request.getRecipientUsername());
        NotificationEnvelope envelope = buildEnvelope(
                request.getType() == null || request.getType().isBlank() ? "client.notification" : request.getType(),
                scope,
                resourceId,
                actorUsername,
                request.getTitle(),
                request.getMessage(),
                NotificationSeverity.from(request.getSeverity()),
                request.getPayload(),
                recipients.size()
        );
        sendToUsers(recipients, envelope);
    }

    public void dispatchSystemNotification(
            String type,
            NotificationScope scope,
            UUID resourceId,
            String title,
            String message,
            NotificationSeverity severity,
            JsonNode payload) {

        Set<String> recipients = recipientResolver.resolve(scope, resourceId, null);
        if (recipients.isEmpty()) {
            log.info("No notification recipients for {} {}", scope, resourceId);
            return;
        }

        NotificationEnvelope envelope = buildEnvelope(
                type,
                scope,
                resourceId,
                null,
                title,
                message,
                severity,
                payload,
                recipients.size()
        );
        sendToUsers(recipients, envelope);
    }

    public String connectedMessage(String username) {
        NotificationEnvelope envelope = new NotificationEnvelope(
                UUID.randomUUID().toString(),
                "connection.ready",
                "USER",
                null,
                null,
                "Connected",
                "Notification websocket connected for " + username,
                NotificationSeverity.SUCCESS,
                Instant.now(),
                1,
                null
        );
        return serialize(envelope);
    }

    public String errorMessage(String message) {
        NotificationEnvelope envelope = new NotificationEnvelope(
                UUID.randomUUID().toString(),
                "notification.error",
                "USER",
                null,
                null,
                "Notification error",
                message == null || message.isBlank() ? "Invalid notification request" : message,
                NotificationSeverity.ERROR,
                Instant.now(),
                1,
                null
        );
        return serialize(envelope);
    }

    private NotificationEnvelope buildEnvelope(
            String type,
            NotificationScope scope,
            UUID resourceId,
            String actorUsername,
            String title,
            String message,
            NotificationSeverity severity,
            JsonNode payload,
            int recipientCount) {

        return new NotificationEnvelope(
                UUID.randomUUID().toString(),
                type,
                scope.name(),
                resourceId == null ? null : resourceId.toString(),
                actorUsername,
                title,
                message,
                severity,
                Instant.now(),
                recipientCount,
                payload
        );
    }

    private void sendToUsers(Set<String> recipients, NotificationEnvelope envelope) {
        int sessionCount = sessionRegistry.sendToUsers(recipients, serialize(envelope));
        log.info(
                "Notification {} sent to {} users and {} websocket sessions",
                envelope.getType(),
                recipients.size(),
                sessionCount
        );
    }

    private void sendToUser(String username, String payload) {
        sessionRegistry.sendToUsers(Set.of(username), payload);
    }

    private UUID parseResourceId(NotificationScope scope, String resourceId) {
        if (scope == NotificationScope.USER) {
            return null;
        }

        if (resourceId == null || resourceId.isBlank()) {
            throw new IllegalArgumentException("resourceId is required for scope " + scope.name());
        }

        try {
            return UUID.fromString(resourceId);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("resourceId must be a UUID", exception);
        }
    }

    private String serialize(NotificationEnvelope envelope) {
        try {
            return objectMapper.writeValueAsString(envelope);
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to serialize notification envelope", exception);
        }
    }
}
