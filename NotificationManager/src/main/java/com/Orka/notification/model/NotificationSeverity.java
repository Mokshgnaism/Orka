package com.Orka.notification.model;

import java.util.Arrays;

public enum NotificationSeverity {
    INFO,
    SUCCESS,
    WARNING,
    ERROR;

    public static NotificationSeverity from(String value) {
        if (value == null || value.isBlank()) {
            return INFO;
        }

        return Arrays.stream(values())
                .filter(severity -> severity.name().equalsIgnoreCase(value.trim()))
                .findFirst()
                .orElse(INFO);
    }
}
