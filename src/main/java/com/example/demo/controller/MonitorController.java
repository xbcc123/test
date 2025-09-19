package com.example.demo.controller;

import com.example.demo.service.OnlineUserService;
import com.example.demo.model.OperationLog;
import com.example.demo.repository.OperationLogRepository;
import com.example.demo.model.LoginLog;
import com.example.demo.repository.LoginLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import java.util.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@RestController
@RequestMapping("/api/monitor")
public class MonitorController {
    @Autowired
    private OnlineUserService onlineUserService;
    @Autowired
    private OperationLogRepository operationLogRepository;
    @Autowired
    private LoginLogRepository loginLogRepository;
    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    @GetMapping("/online-users")
    public List<Map<String, String>> getOnlineUsers() {
        return onlineUserService.getOnlineUsers();
    }

    @GetMapping("/operation-logs")
    public Page<OperationLog> getOperationLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String method,
            @RequestParam(required = false) String uri,
            @RequestParam(required = false) String ip,
            @RequestParam(required = false) String result,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        Pageable pageable = PageRequest.of(page, size);
        return operationLogRepository.findAll((Root<OperationLog> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            java.util.List<Predicate> predicates = new java.util.ArrayList<>();
            if (username != null && !username.isEmpty())
                predicates.add(cb.equal(root.get("username"), username));
            if (method != null && !method.isEmpty())
                predicates.add(cb.equal(root.get("method"), method));
            if (uri != null && !uri.isEmpty())
                predicates.add(cb.like(root.get("uri"), "%" + uri + "%"));
            if (ip != null && !ip.isEmpty())
                predicates.add(cb.equal(root.get("ip"), ip));
            if (result != null && !result.isEmpty())
                predicates.add(cb.like(root.get("result"), "%" + result + "%"));
            if (startTime != null && !startTime.isEmpty())
                predicates.add(cb.greaterThanOrEqualTo(root.get("time"), java.time.LocalDateTime.parse(startTime)));
            if (endTime != null && !endTime.isEmpty())
                predicates.add(cb.lessThanOrEqualTo(root.get("time"), java.time.LocalDateTime.parse(endTime)));
            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable);
    }

    @GetMapping("/login-logs")
    public Page<LoginLog> getLoginLogs(@RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return loginLogRepository.findAll(pageable);
    }

    @GetMapping("/redis-info")
    public Map<String, Object> getRedisInfo() {
        var conn = redisConnectionFactory.getConnection();
        Map<String, Object> info = new HashMap<>();
        try {
            Properties props = conn.info();
            info.put("dbSize", conn.dbSize());
            info.put("usedMemory", props.getProperty("used_memory_human"));
            info.put("connectedClients", props.getProperty("connected_clients"));
            info.put("uptime", props.getProperty("uptime_in_seconds"));
            info.put("version", props.getProperty("redis_version"));
        } finally {
            conn.close();
        }
        return info;
    }
}
