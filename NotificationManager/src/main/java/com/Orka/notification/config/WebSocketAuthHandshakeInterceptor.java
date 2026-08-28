package com.Orka.notification.config;

import com.Orka.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

@Component
public class WebSocketAuthHandshakeInterceptor implements HandshakeInterceptor {
    public static final String USERNAME_ATTRIBUTE = "username";
    public static final String EMAIL_ATTRIBUTE = "email";

    private final JwtUtil jwtUtil;

    public WebSocketAuthHandshakeInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes) {

        Optional<String> token = extractToken(request);
        if (token.isEmpty()) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }

        try {
            Claims claims = jwtUtil.getClaims(token.get());
            String username = claims.get("username", String.class);
            String email = claims.get("email", String.class);

            if (username == null || username.isBlank()) {
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return false;
            }

            attributes.put(USERNAME_ATTRIBUTE, username);
            attributes.put(EMAIL_ATTRIBUTE, email);
            return true;
        } catch (Exception ignored) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }
    }

    @Override
    public void afterHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Exception exception) {
    }

    private Optional<String> extractToken(ServerHttpRequest request) {
        Optional<String> headerToken = extractBearerToken(request);
        if (headerToken.isPresent()) {
            return headerToken;
        }

        Optional<String> cookieToken = extractCookieToken(request);
        if (cookieToken.isPresent()) {
            return cookieToken;
        }

        return extractQueryToken(request);
    }

    private Optional<String> extractBearerToken(ServerHttpRequest request) {
        String authorization = request.getHeaders().getFirst("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return Optional.empty();
        }
        return Optional.of(authorization.substring("Bearer ".length()).trim());
    }

    private Optional<String> extractCookieToken(ServerHttpRequest request) {
        if (!(request instanceof ServletServerHttpRequest servletRequest)) {
            return Optional.empty();
        }

        HttpServletRequest httpRequest = servletRequest.getServletRequest();
        Cookie[] cookies = httpRequest.getCookies();
        if (cookies == null) {
            return Optional.empty();
        }

        return Arrays.stream(cookies)
                .filter(cookie -> "JWT_COOKIE".equals(cookie.getName()) || "JWT".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst();
    }

    private Optional<String> extractQueryToken(ServerHttpRequest request) {
        String query = request.getURI().getRawQuery();
        if (query == null || query.isBlank()) {
            return Optional.empty();
        }

        return Arrays.stream(query.split("&"))
                .map(parameter -> parameter.split("=", 2))
                .filter(parts -> parts.length == 2 && "token".equals(parts[0]))
                .map(parts -> URLDecoder.decode(parts[1], StandardCharsets.UTF_8))
                .findFirst();
    }
}
