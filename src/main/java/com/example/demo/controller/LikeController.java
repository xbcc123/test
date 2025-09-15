package com.example.demo.controller;

import com.example.demo.model.CustomUserDetails;
import com.example.demo.model.Like;
import com.example.demo.repository.LikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("/likes")
public class LikeController {
    @Autowired
    private LikeRepository likeRepository;

    @GetMapping("/count")
    public Map<String, Long> count(@RequestParam Long postId) {
        long count = likeRepository.countByPostId(postId);
        Map<String, Long> result = new HashMap<>();
        result.put("count", count);
        return result;
    }

    @GetMapping("/check")
    public Map<String, Boolean> check(@RequestParam Long postId) {
        Long userId = getCurrentUserId();
        boolean liked = likeRepository.findByPostIdAndUserId(postId, userId).isPresent();
        Map<String, Boolean> result = new HashMap<>();
        result.put("liked", liked);
        return result;
    }

    @PostMapping
    public Like like(@RequestBody Like like) {
        like.setUserId(getCurrentUserId());
        return likeRepository.save(like);
    }

    @DeleteMapping
    @Transactional
    public void unlike(@RequestParam Long postId) {
        likeRepository.deleteByPostIdAndUserId(postId, getCurrentUserId());
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("未登录");
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails) {
            return ((CustomUserDetails) principal).getUserId();
        }
        throw new RuntimeException("无法获取用户ID");
    }
}
