package com.Orka.notification.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@Component
public class NotificationSessionRegistry {
    private final ConcurrentMap<String, Set<WebSocketSession>> sessionsByUsername = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, String> usernameBySessionId = new ConcurrentHashMap<>();

    public void register(String username, WebSocketSession session) {
        sessionsByUsername.computeIfAbsent(username, ignored -> ConcurrentHashMap.newKeySet()).add(session);
        usernameBySessionId.put(session.getId(), username);
        log.info("Notification websocket connected for {}", username);
    }

    public void unregister(WebSocketSession session) {
        String username = usernameBySessionId.remove(session.getId());
        if (username == null) {
            return;
        }

        Set<WebSocketSession> sessions = sessionsByUsername.get(username);
        if (sessions != null) {
            sessions.remove(session);
            if (sessions.isEmpty()) {
                sessionsByUsername.remove(username);
            }
        }
        log.info("Notification websocket disconnected for {}", username);
    }

    public int sendToUsers(Collection<String> usernames, String payload) {
        int sentCount = 0;
        for (String username : usernames) {
            Set<WebSocketSession> sessions = sessionsByUsername.get(username);
            if (sessions == null || sessions.isEmpty()) {
                continue;
            }

            for (WebSocketSession session : sessions) {
                if (send(session, payload)) {
                    sentCount++;
                }
            }
        }
        return sentCount;
    }

    public boolean send(WebSocketSession session, String payload) {
        if (!session.isOpen()) {
            unregister(session);
            return false;
        }

        try {
            synchronized (session) {
                session.sendMessage(new TextMessage(payload));
            }
            return true;
        } catch (IOException exception) {
            log.warn("Unable to send websocket notification to session {}", session.getId(), exception);
            unregister(session);
            return false;
        }
    }
}
