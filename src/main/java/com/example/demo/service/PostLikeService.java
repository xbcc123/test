package com.example.demo.service;

import com.example.demo.model.PostLike;
import com.example.demo.repository.PostLikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostLikeService {
    @Autowired
    private PostLikeRepository postLikeRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Transactional
    public boolean like(Long userId, Long postId) {
        if (postLikeRepository.existsByUserIdAndPostId(userId, postId)) {
            return false;
        }
        PostLike like = new PostLike();
        like.setUserId(userId);
        like.setPostId(postId);
        postLikeRepository.save(like);
        // Redis计数器+1
        String redisKey = "post:like:count:" + postId;
        redisTemplate.opsForValue().increment(redisKey, 1);
        return true;
    }

    @Transactional
    public boolean unlike(Long userId, Long postId) {
        PostLike like = postLikeRepository.findByUserIdAndPostId(userId, postId);
        if (like != null) {
            postLikeRepository.delete(like);
            // Redis计数器-1
            String redisKey = "post:like:count:" + postId;
            redisTemplate.opsForValue().decrement(redisKey, 1);
            return true;
        }
        return false;
    }

    public boolean isLiked(Long userId, Long postId) {
        return postLikeRepository.existsByUserIdAndPostId(userId, postId);
    }

    public long countLikes(Long postId) {
        String redisKey = "post:like:count:" + postId;
        Object val = redisTemplate.opsForValue().get(redisKey);
        if (val instanceof Number) {
            return ((Number) val).longValue();
        }
        long count = postLikeRepository.countByPostId(postId);
        redisTemplate.opsForValue().set(redisKey, count);
        return count;
    }
}
