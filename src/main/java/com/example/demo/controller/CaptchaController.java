package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/auth")
public class CaptchaController {
    // 简单内存存储，生产建议用 Redis
    private static final Map<String, String> captchaStore = new ConcurrentHashMap<>();

    @GetMapping("/captcha")
    public ResponseEntity<Map<String, String>> getCaptcha() {
        String code = generateCode(5);
        String uuid = UUID.randomUUID().toString();
        captchaStore.put(uuid, code.toLowerCase());
        String base64Img = generateCaptchaImageBase64(code);
        return ResponseEntity.ok(Map.of("uuid", uuid, "img", base64Img));
    }

    public static boolean validateCaptcha(String uuid, String code) {
        if (uuid == null || code == null) return false;
        String real = captchaStore.get(uuid);
        if (real != null && real.equals(code.toLowerCase())) {
            captchaStore.remove(uuid);
            return true;
        }
        return false;
    }

    private String generateCode(int len) {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt((int) (Math.random() * chars.length())));
        }
        return sb.toString();
    }

    private String generateCaptchaImageBase64(String code) {
        int w = 120, h = 40;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, w, h);
        g.setFont(new Font("Arial", Font.BOLD, 28));
        g.setColor(Color.BLACK);
        g.drawString(code, 18, 30);
        g.dispose();
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(img, "png", baos);
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (Exception e) {
            return null;
        }
    }
}

