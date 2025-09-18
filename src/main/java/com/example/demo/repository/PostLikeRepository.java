package com.example.demo.repository;

import com.example.demo.model.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    boolean existsByUserIdAndPostId(Long userId, Long postId);
    long countByPostId(Long postId);
    PostLike findByUserIdAndPostId(Long userId, Long postId);
}

