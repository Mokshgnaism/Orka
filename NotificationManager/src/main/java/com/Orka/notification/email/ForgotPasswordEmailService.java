package com.Orka.notification.email;

import com.Orka.repository.UserRepository;
import com.Orka.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
public class ForgotPasswordEmailService {
    private static final String ACCEPTED_MESSAGE = "If the account exists, a password reset email will be sent.";

    private final UserRepository userRepository;
    private final ObjectProvider<JavaMailSender> mailSenderProvider;
    private final boolean mailEnabled;
    private final String mailFrom;
    private final String subject;
    private final String resetUrlTemplate;

    public ForgotPasswordEmailService(
            UserRepository userRepository,
            ObjectProvider<JavaMailSender> mailSenderProvider,
            @Value("${notification.mail.enabled:false}") boolean mailEnabled,
            @Value("${notification.mail.from:no-reply@orka.local}") String mailFrom,
            @Value("${notification.forgot-password.subject:Reset your Orka password}") String subject,
            @Value("${notification.forgot-password.reset-url-template:http://localhost:8080/reset-password?email={email}}") String resetUrlTemplate) {
        this.userRepository = userRepository;
        this.mailSenderProvider = mailSenderProvider;
        this.mailEnabled = mailEnabled;
        this.mailFrom = mailFrom;
        this.subject = subject;
        this.resetUrlTemplate = resetUrlTemplate;
    }

    @Transactional(readOnly = true)
    public ForgotPasswordResponse sendForgotPasswordEmail(ForgotPasswordRequest request) {
        if (request == null || bothBlank(request.getEmail(), request.getUsername())) {
            return new ForgotPasswordResponse(false, "email or username is required");
        }

        Optional<User> user = findUser(request);
        if (user.isEmpty() || isBlank(user.get().getEmail())) {
            log.info("Forgot password email accepted for unknown or email-less account");
            return new ForgotPasswordResponse(true, ACCEPTED_MESSAGE);
        }

        if (!mailEnabled) {
            log.info("Forgot password email accepted for {} but mail delivery is disabled", user.get().getUsername());
            return new ForgotPasswordResponse(true, ACCEPTED_MESSAGE);
        }

        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null) {
            log.warn("Forgot password email accepted but JavaMailSender is unavailable");
            return new ForgotPasswordResponse(true, ACCEPTED_MESSAGE);
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailFrom);
        message.setTo(user.get().getEmail());
        message.setSubject(subject);
        message.setText(emailBody(user.get(), request));
        mailSender.send(message);

        log.info("Forgot password email sent for {}", user.get().getUsername());
        return new ForgotPasswordResponse(true, ACCEPTED_MESSAGE);
    }

    private Optional<User> findUser(ForgotPasswordRequest request) {
        if (!isBlank(request.getUsername())) {
            return userRepository.findByUsername(request.getUsername());
        }
        return userRepository.findByEmail(request.getEmail());
    }

    private String emailBody(User user, ForgotPasswordRequest request) {
        String resetUrl = isBlank(request.getResetUrl())
                ? resetUrlTemplate.replace("{email}", user.getEmail()).replace("{username}", user.getUsername())
                : request.getResetUrl();

        return """
                We received a request to reset your Orka password.

                Username: %s
                Reset link: %s

                If you did not request this, you can ignore this email.
                """.formatted(user.getUsername(), resetUrl);
    }

    private boolean bothBlank(String first, String second) {
        return isBlank(first) && isBlank(second);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
