package com.example.demo.service;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatSessionManager {
    private final Map<Long, Set<WebSocketSession>> userSessions = new ConcurrentHashMap<>();

    public void addSession(Long userId, WebSocketSession session) {
        userSessions.computeIfAbsent(userId, k -> Collections.newSetFromMap(new ConcurrentHashMap<>()))
                .add(session);
    }

    public void removeSession(Long userId, WebSocketSession session) {
        Set<WebSocketSession> set = userSessions.get(userId);
        if (set != null) {
            set.remove(session);
            if (set.isEmpty()) userSessions.remove(userId);
        }
    }

    public Set<WebSocketSession> getSessions(Long userId) {
        return userSessions.getOrDefault(userId, Collections.emptySet());
    }

    public boolean isOnline(Long userId) {
        return !getSessions(userId).isEmpty();
    }
}

