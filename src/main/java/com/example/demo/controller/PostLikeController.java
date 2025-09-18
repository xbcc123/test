package com.example.demo.controller;

import com.example.demo.service.PostLikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/post/{postId}/like")
public class PostLikeController {
    @Autowired
    private PostLikeService postLikeService;

    @PostMapping
    public ResponseEntity<?> like(@PathVariable Long postId, @RequestParam Long userId) {
        boolean success = postLikeService.like(userId, postId);
        if (success) {
            return ResponseEntity.ok(com.example.demo.model.ApiResponse.success("点赞成功"));
        } else {
            return ResponseEntity.ok(com.example.demo.model.ApiResponse.error(400, "已点赞"));
        }
    }

    @DeleteMapping
    public ResponseEntity<?> unlike(@PathVariable Long postId, @RequestParam Long userId) {
        boolean success = postLikeService.unlike(userId, postId);
        if (success) {
            return ResponseEntity.ok(com.example.demo.model.ApiResponse.success("取消点赞成功"));
        } else {
            return ResponseEntity.ok(com.example.demo.model.ApiResponse.error(400, "未点赞"));
        }
    }

    @GetMapping("/status")
    public ResponseEntity<?> isLiked(@PathVariable Long postId, @RequestParam Long userId) {
        boolean liked = postLikeService.isLiked(userId, postId);
        return ResponseEntity.ok().body(new java.util.HashMap<String, Object>() {{
            put("liked", liked);
        }});
    }

    @GetMapping("/count")
    public ResponseEntity<?> count(@PathVariable Long postId) {
        long count = postLikeService.countLikes(postId);
        return ResponseEntity.ok(count);
    }
}
