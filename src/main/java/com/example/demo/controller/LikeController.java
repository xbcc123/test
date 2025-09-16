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

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/likes")
@Tag(name = "点赞接口", description = "点赞相关操作")
public class LikeController {
    @Autowired
    private LikeRepository likeRepository;

    @GetMapping("/count")
    @Operation(summary = "获取点赞数", description = "根据帖子ID获取点赞总数")
    public Map<String, Long> count(@RequestParam Long postId) {
        long count = likeRepository.countByPostId(postId);
        Map<String, Long> result = new HashMap<>();
        result.put("count", count);
        return result;
    }

    @GetMapping("/check")
    @Operation(summary = "检查是否点赞", description = "检查当前用户是否已点赞该帖子")
    public Map<String, Boolean> check(@RequestParam Long postId) {
        Long userId = getCurrentUserId();
        boolean liked = likeRepository.findByPostIdAndUserId(postId, userId).isPresent();
        Map<String, Boolean> result = new HashMap<>();
        result.put("liked", liked);
        return result;
    }

    @PostMapping
    @Operation(summary = "点赞", description = "对帖子进行点赞")
    public Like like(@RequestBody Like like) {
        like.setUserId(getCurrentUserId());
        return likeRepository.save(like);
    }

    @DeleteMapping
    @Transactional
    @Operation(summary = "取消点赞", description = "对帖子取消点赞")
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
