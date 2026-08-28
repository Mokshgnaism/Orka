package com.Orka.notification.email;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
public class ForgotPasswordController {
    private final ForgotPasswordEmailService forgotPasswordEmailService;

    public ForgotPasswordController(ForgotPasswordEmailService forgotPasswordEmailService) {
        this.forgotPasswordEmailService = forgotPasswordEmailService;
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ForgotPasswordResponse> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        ForgotPasswordResponse response = forgotPasswordEmailService.sendForgotPasswordEmail(request);
        return response.isAccepted()
                ? ResponseEntity.accepted().body(response)
                : ResponseEntity.badRequest().body(response);
    }
}
