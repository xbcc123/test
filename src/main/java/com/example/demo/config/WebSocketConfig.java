package com.example.demo.config;

import com.example.demo.service.JwtUtil;
import com.example.demo.websocket.ChatWebSocketHandler;
import com.example.demo.websocket.JwtHandshakeInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    @Autowired
    private ChatWebSocketHandler chatWebSocketHandler;
    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(chatWebSocketHandler, "/ws/chat")
                .addInterceptors(new JwtHandshakeInterceptor(jwtUtil))
                .setAllowedOrigins("*");
    }
}

