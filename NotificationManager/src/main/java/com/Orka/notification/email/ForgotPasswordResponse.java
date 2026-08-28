package com.Orka.notification.email;

public class ForgotPasswordResponse {
    private final boolean accepted;
    private final String message;

    public ForgotPasswordResponse(boolean accepted, String message) {
        this.accepted = accepted;
        this.message = message;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public String getMessage() {
        return message;
    }
}
