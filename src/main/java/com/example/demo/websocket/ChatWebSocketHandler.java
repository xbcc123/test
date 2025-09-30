package com.example.demo.websocket;

import com.example.demo.model.dto.ChatMessageDTO;
import com.example.demo.service.ChatService;
import com.example.demo.service.ChatSessionManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {
    private static final Logger log = LoggerFactory.getLogger(ChatWebSocketHandler.class);

    @Autowired
    private ChatService chatService;
    @Autowired
    private ChatSessionManager sessionManager;
    @Autowired
    private ObjectMapper objectMapper;

    // 保存最近心跳时间以便后续扩展
    private final Map<WebSocketSession, LocalDateTime> heartbeats = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long userId = (Long) session.getAttributes().get("userId");
        if (userId == null) {
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("No Auth"));
            return;
        }
        sessionManager.addSession(userId, session);
        heartbeats.put(session, LocalDateTime.now());
        log.info("User {} connected, total sessions: {}", userId, sessionManager.getSessions(userId).size());
        sendSystem(session, "CONNECTED");
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Long userId = (Long) session.getAttributes().get("userId");
        if (userId == null) {
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("No Auth"));
            return;
        }
        JsonNode root = objectMapper.readTree(message.getPayload());
        String type = root.path("type").asText();
        switch (type) {
            case "chat" -> handleChat(userId, root, session);
            case "ping" -> {
                heartbeats.put(session, LocalDateTime.now());
                sendSimple(session, "pong");
            }
            default -> sendError(session, "UNKNOWN_TYPE");
        }
    }

    private void handleChat(Long fromUserId, JsonNode root, WebSocketSession session) throws JsonProcessingException, IOException {
        Long toUserId = root.path("toUserId").asLong();
        String content = root.path("content").asText();
        if (toUserId == null || toUserId <= 0 || content == null || content.isEmpty()) {
            sendError(session, "INVALID_PAYLOAD");
            return;
        }
        ChatMessageDTO saved = chatService.sendMessage(fromUserId, toUserId, content);
        // 构造广播消息
        String json = objectMapper.writeValueAsString(Map.of(
                "type", "chat",
                "data", saved
        ));
        // 发给发送者自身所有连接
        broadcastToUser(fromUserId, json);
        // 发给目标用户
        broadcastToUser(toUserId, json);
    }

    private void broadcastToUser(Long userId, String json) throws IOException {
        for (WebSocketSession s : sessionManager.getSessions(userId)) {
            if (s.isOpen()) {
                s.sendMessage(new TextMessage(json));
            }
        }
    }

    private void sendSystem(WebSocketSession session, String event) throws IOException {
        send(session, Map.of("type", "system", "event", event));
    }

    private void sendError(WebSocketSession session, String err) throws IOException {
        send(session, Map.of("type", "error", "error", err));
    }

    private void sendSimple(WebSocketSession session, String msg) throws IOException {
        send(session, Map.of("type", msg));
    }

    private void send(WebSocketSession session, Map<String, Object> map) throws IOException {
        if (session.isOpen()) {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(map)));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Long userId = (Long) session.getAttributes().get("userId");
        if (userId != null) {
            sessionManager.removeSession(userId, session);
            heartbeats.remove(session);
            log.info("User {} disconnected", userId);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.warn("Transport error: {}", exception.getMessage());
    }
}

