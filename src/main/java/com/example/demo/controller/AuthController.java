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
}

