package com.example.demo.controller;

import com.example.demo.model.LoginLog;
import com.example.demo.model.User;
import com.example.demo.service.JwtUtil;
import com.example.demo.service.UserService;
import com.example.demo.repository.LoginLogRepository;
import com.example.demo.service.OnlineUserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@Tag(name = "认证接口", description = "登录、注册等认证相关操作")
public class AuthController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private LoginLogRepository loginLogRepository;
    @Autowired
    private OnlineUserService onlineUserService;

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户登录并返回JWT Token")
    public ResponseEntity<?> login(@RequestBody Map<String, Object> req, HttpServletRequest request) {
        String username = (String) req.get("username");
        String password = (String) req.get("password");
        String captcha = (String) req.get("captcha");
        String uuid = (String) req.get("uuid");
        String ip = request.getRemoteAddr();
        if (!CaptchaController.validateCaptcha(uuid, captcha)) {
            LoginLog log = new LoginLog();
            log.setUsername(username);
            log.setIp(ip);
            log.setTime(java.time.LocalDateTime.now());
            log.setSuccess(false);
            log.setMessage("验证码错误");
            loginLogRepository.save(log);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "验证码错误"));
        }
        User validUser = userService.validateUser(username, password);
        if (validUser != null) {
            String token = jwtUtil.generateToken(validUser.getId(), validUser.getUsername());
            onlineUserService.online(String.valueOf(validUser.getId()), validUser.getUsername());
            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            result.put("userId", validUser.getId());
            result.put("username", validUser.getUsername());
            result.put("roles", validUser.getRoles());
            result.put("nickname", validUser.getNickname());
            // 登录成功日志
            LoginLog log = new LoginLog();
            log.setUsername(username);
            log.setIp(ip);
            log.setTime(java.time.LocalDateTime.now());
            log.setSuccess(true);
            log.setMessage("登录成功");
            loginLogRepository.save(log);
            return ResponseEntity.ok(result);
        } else {
            // 登录失败日志
            LoginLog log = new LoginLog();
            log.setUsername(username);
            log.setIp(ip);
            log.setTime(java.time.LocalDateTime.now());
            log.setSuccess(false);
            log.setMessage("用户名或密码错误");
            loginLogRepository.save(log);
            Map<String, String> result = new HashMap<>();
            result.put("error", "用户名或密码错误");
            return ResponseEntity.status(401).body(result);
        }
    }

    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "注册新用户")
    public ResponseEntity<?> register(@RequestBody User user) {
        boolean ok = userService.registerUser(user);
        if (ok) {
            return ResponseEntity.ok().body("注册成功");
        } else {
            return ResponseEntity.status(409).body("用户名已存在");
        }
    }

    @PostMapping("/reset-password")
    @Operation(summary = "重置密码", description = "为用户重置密码")
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
