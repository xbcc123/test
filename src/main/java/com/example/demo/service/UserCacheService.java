package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.model.dto.BasicUserDTO;
import com.example.demo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserCacheService {
    private static final Logger log = LoggerFactory.getLogger(UserCacheService.class);
    private static final String KEY_PREFIX = "user:basic:";
    private static final Duration TTL = Duration.ofMinutes(10);

    @Autowired
    private UserRepository userRepository;

    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate; // 允许缺失（测试场景）

    private final Map<Long, BasicUserDTO> localCache = new ConcurrentHashMap<>();

    public BasicUserDTO getBasicUser(Long userId) {
        if (userId == null) return null;
        // 先查本地缓存
        BasicUserDTO local = localCache.get(userId);
        if (local != null) return local;
        // 再查 Redis
        if (redisTemplate != null) {
            try {
                Object obj = redisTemplate.opsForValue().get(KEY_PREFIX + userId);
                if (obj instanceof BasicUserDTO dto) {
                    localCache.put(userId, dto);
                    return dto;
                }
            } catch (Exception e) {
                log.debug("Redis 获取用户信息失败: {}", e.getMessage());
            }
        }
        // 查询数据库
        Optional<User> opt = userRepository.findById(userId);
        BasicUserDTO dto = opt.map(u -> new BasicUserDTO(u.getId(), u.getUsername(), u.getNickname(), u.getAvatar()))
                .orElse(new BasicUserDTO(userId, "user" + userId, "用户" + userId, null));
        // 写入缓存
        localCache.put(userId, dto);
        if (redisTemplate != null) {
            try {
                redisTemplate.opsForValue().set(KEY_PREFIX + userId, dto, TTL);
            } catch (Exception e) {
                log.debug("Redis 缓存用户信息失败: {}", e.getMessage());
            }
        }
        return dto;
    }

    public void evict(Long userId) {
        if (userId == null) return;
        localCache.remove(userId);
        if (redisTemplate != null) {
            try { redisTemplate.delete(KEY_PREFIX + userId); } catch (Exception ignored) {}
        }
    }
}

