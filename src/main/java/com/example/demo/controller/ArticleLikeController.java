package com.example.demo.controller;

import com.example.demo.model.CustomUserDetails;
import com.example.demo.service.ArticleLikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/article/{articleId}/like")
public class ArticleLikeController {
    @Autowired
    private ArticleLikeService articleLikeService;

    @PostMapping
    public ResponseEntity<?> like(@PathVariable Long articleId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) return ResponseEntity.status(401).body("请先登录");
        boolean result = articleLikeService.likeArticle(articleId, userDetails.getUsername());
        return result ? ResponseEntity.ok().build() : ResponseEntity.badRequest().body("已点赞或参数错误");
    }

    @DeleteMapping
    public ResponseEntity<?> unlike(@PathVariable Long articleId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) return ResponseEntity.status(401).body("请先登录");
        boolean result = articleLikeService.unlikeArticle(articleId, userDetails.getUsername());
        return result ? ResponseEntity.ok().build() : ResponseEntity.badRequest().body("未点赞或参数错误");
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getLikeCount(@PathVariable Long articleId) {
        long count = articleLikeService.getLikeCount(articleId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/status")
    public ResponseEntity<Boolean> isLiked(@PathVariable Long articleId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) return ResponseEntity.ok(false);
        boolean liked = articleLikeService.isLikedByUser(articleId, userDetails.getUsername());
        return ResponseEntity.ok(liked);
    }
}
