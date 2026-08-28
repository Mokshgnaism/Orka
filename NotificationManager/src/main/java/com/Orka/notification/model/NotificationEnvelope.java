package com.Orka.notification.model;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.Instant;

public class NotificationEnvelope {
    private final String id;
    private final String type;
    private final String scope;
    private final String resourceId;
    private final String actorUsername;
    private final String title;
    private final String message;
    private final NotificationSeverity severity;
    private final Instant createdAt;
    private final int recipientCount;
    private final JsonNode payload;

    public NotificationEnvelope(
            String id,
            String type,
            String scope,
            String resourceId,
            String actorUsername,
            String title,
            String message,
            NotificationSeverity severity,
            Instant createdAt,
            int recipientCount,
            JsonNode payload) {
        this.id = id;
        this.type = type;
        this.scope = scope;
        this.resourceId = resourceId;
        this.actorUsername = actorUsername;
        this.title = title;
        this.message = message;
        this.severity = severity;
        this.createdAt = createdAt;
        this.recipientCount = recipientCount;
        this.payload = payload;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getScope() {
        return scope;
    }

    public String getResourceId() {
        return resourceId;
    }

    public String getActorUsername() {
        return actorUsername;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public NotificationSeverity getSeverity() {
        return severity;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public int getRecipientCount() {
        return recipientCount;
    }

    public JsonNode getPayload() {
        return payload;
    }
}
