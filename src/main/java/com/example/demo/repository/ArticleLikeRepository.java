package com.example.demo.repository;

import com.example.demo.model.Article;
import com.example.demo.model.ArticleLike;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ArticleLikeRepository extends JpaRepository<ArticleLike, Long> {
    Optional<ArticleLike> findByArticleAndUser(Article article, User user);
    long countByArticle(Article article);
    void deleteByArticleAndUser(Article article, User user);
    boolean existsByArticleAndUser(Article article, User user);

    @Query("SELECT COUNT(al) FROM ArticleLike al WHERE al.article.author.id = :userId")
    long countLikesByUserId(Long userId);
}
