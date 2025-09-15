package com.example.demo.controller;

import com.example.demo.model.Like;
import com.example.demo.repository.LikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
    public Map<String, Boolean> check(@RequestParam Long postId, @RequestParam Long userId) {
        boolean liked = likeRepository.findByPostIdAndUserId(postId, userId).isPresent();
        Map<String, Boolean> result = new HashMap<>();
        result.put("liked", liked);
        return result;
    }

    @PostMapping
    public Like like(@RequestBody Like like) {
        return likeRepository.save(like);
    }

    @DeleteMapping
    @Transactional
    public void unlike(@RequestParam Long postId, @RequestParam Long userId) {
        likeRepository.deleteByPostIdAndUserId(postId, userId);
    }
}
