package com.example.demo.websocket;

import com.example.demo.service.JwtUtil;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

public class JwtHandshakeInterceptor implements HandshakeInterceptor {
    private final JwtUtil jwtUtil;

    public JwtHandshakeInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        // token 通过 query 参数传递 ?token=xxx 或 header Authorization: Bearer xxx
        String query = request.getURI().getQuery();
        String token = null;
        if (query != null) {
            for (String kv : query.split("&")) {
                String[] arr = kv.split("=",2);
                if (arr.length==2 && arr[0].equals("token")) {
                    token = arr[1];
                    break;
                }
            }
        }
        if (token == null) {
            // 尝试从 headers
            String auth = request.getHeaders().getFirst("Authorization");
            if (auth != null && auth.startsWith("Bearer ")) token = auth.substring(7);
        }
        if (token != null) {
            try {
                if (jwtUtil.validateToken(token)) {
                    Long userId = jwtUtil.getUserIdFromToken(token);
                    attributes.put("userId", userId);
                    return true;
                }
            } catch (Exception ignored) {}
        }
        return false; // 拒绝握手
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, @Nullable Exception exception) {
    }
}

