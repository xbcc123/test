package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OnlineUserService {
    private static final String ONLINE_USER_KEY = "online:user:";
    private static final Duration ONLINE_TIMEOUT = Duration.ofMinutes(30);

    @Autowired
    private StringRedisTemplate redisTemplate;

    public void online(String userId, String username) {
        String key = ONLINE_USER_KEY + userId;
        redisTemplate.opsForHash().put(key, "username", username);
        redisTemplate.opsForHash().put(key, "lastActive", String.valueOf(System.currentTimeMillis()));
        redisTemplate.expire(key, ONLINE_TIMEOUT);
    }

    public void offline(String userId) {
        redisTemplate.delete(ONLINE_USER_KEY + userId);
    }

    public List<Map<String, String>> getOnlineUsers() {
        Set<String> keys = redisTemplate.keys(ONLINE_USER_KEY + "*");
        if (keys == null) return Collections.emptyList();
        List<Map<String, String>> users = new ArrayList<>();
        for (String key : keys) {
            Map<Object, Object> map = redisTemplate.opsForHash().entries(key);
            if (!map.isEmpty()) {
                Map<String, String> user = new HashMap<>();
                user.put("userId", key.substring(ONLINE_USER_KEY.length()));
                user.put("username", (String) map.get("username"));
                user.put("lastActive", (String) map.get("lastActive"));
                users.add(user);
            }
        }
        // 按活跃时间倒序
        return users.stream().sorted((a, b) -> b.get("lastActive").compareTo(a.get("lastActive"))).collect(Collectors.toList());
    }
}

