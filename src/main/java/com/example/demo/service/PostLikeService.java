package com.example.demo.service;

import com.example.demo.model.PostLike;
import com.example.demo.repository.PostLikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostLikeService {
    @Autowired
    private PostLikeRepository postLikeRepository;

    @Transactional
    public boolean like(Long userId, Long postId) {
        if (postLikeRepository.existsByUserIdAndPostId(userId, postId)) {
            return false;
        }
        PostLike like = new PostLike();
        like.setUserId(userId);
        like.setPostId(postId);
        postLikeRepository.save(like);
        return true;
    }

    @Transactional
    public boolean unlike(Long userId, Long postId) {
        PostLike like = postLikeRepository.findByUserIdAndPostId(userId, postId);
        if (like != null) {
            postLikeRepository.delete(like);
            return true;
        }
        return false;
    }

    public boolean isLiked(Long userId, Long postId) {
        return postLikeRepository.existsByUserIdAndPostId(userId, postId);
    }

    public long countLikes(Long postId) {
        return postLikeRepository.countByPostId(postId);
    }
}
