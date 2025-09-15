package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.JwtUtil;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class AuthController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        User validUser = userService.validateUser(user.getUsername(), user.getPassword());
        if (validUser != null) {
            String token = jwtUtil.generateToken(validUser.getUsername());
            Map<String, String> result = new HashMap<>();
            result.put("token", token);
            return ResponseEntity.ok(result);
        } else {
            Map<String, String> result = new HashMap<>();
            result.put("error", "用户名或密码错误");
            return ResponseEntity.status(401).body(result);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        boolean ok = userService.registerUser(user);
        if (ok) {
            return ResponseEntity.ok().body("注册成功");
        } else {
            return ResponseEntity.status(409).body("用户名已存在");
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> req) {
        String username = req.get("username");
        String newPassword = req.get("newPassword");
        boolean ok = userService.resetPassword(username, newPassword);
        if (ok) {
            return ResponseEntity.ok().body("密码重置成功");
        } else {
            return ResponseEntity.status(404).body("用户不存在");
        }
    }
}
