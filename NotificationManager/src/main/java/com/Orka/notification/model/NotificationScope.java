package com.Orka.notification.model;

import java.util.Arrays;

public enum NotificationScope {
    WORKFLOW_RUN,
    TASK_RUN,
    STATE_RUN,
    WORKFLOW_DEFINITION,
    TASK_DEFINITION,
    USER;

    public static NotificationScope from(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("notification scope is required");
        }

        return Arrays.stream(values())
                .filter(scope -> scope.name().equalsIgnoreCase(value.trim().replace('-', '_')))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("unsupported notification scope: " + value));
    }
}
