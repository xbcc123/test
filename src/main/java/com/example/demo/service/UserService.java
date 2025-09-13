package com.example.demo.service;

import com.example.demo.model.User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {
    // 简单内存用户数据，实际可用数据库
    private static final Map<String, String> USER_MAP = new HashMap<>();
    static {
        USER_MAP.put("admin", "admin123");
        USER_MAP.put("user", "user123");
    }

    public User validateUser(String username, String password) {
        String pwd = USER_MAP.get(username);
        if (pwd != null && pwd.equals(password)) {
            return new User(username, null); // 不返回密码
        }
        return null;
    }
}

