package com.example.demo.service;

import com.example.demo.model.Article;
import com.example.demo.model.ArticleLike;
import com.example.demo.model.User;
import com.example.demo.repository.ArticleLikeRepository;
import com.example.demo.repository.ArticleRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ArticleLikeService {
    @Autowired
    private ArticleLikeRepository articleLikeRepository;
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public boolean likeArticle(Long articleId, String username) {
        Article article = articleRepository.findById(articleId).orElse(null);
        User user = userRepository.findByUsername(username).orElse(null);
        if (article == null || user == null) return false;
        if (articleLikeRepository.existsByArticleAndUser(article, user)) return false;
        ArticleLike like = new ArticleLike();
        like.setArticle(article);
        like.setUser(user);
        articleLikeRepository.save(like);
        return true;
    }

    @Transactional
    public boolean unlikeArticle(Long articleId, String username) {
        Article article = articleRepository.findById(articleId).orElse(null);
        User user = userRepository.findByUsername(username).orElse(null);
        if (article == null || user == null) return false;
        if (!articleLikeRepository.existsByArticleAndUser(article, user)) return false;
        articleLikeRepository.deleteByArticleAndUser(article, user);
        return true;
    }

    public long getLikeCount(Long articleId) {
        Article article = articleRepository.findById(articleId).orElse(null);
        if (article == null) return 0;
        return articleLikeRepository.countByArticle(article);
    }

    public boolean isLikedByUser(Long articleId, String username) {
        Article article = articleRepository.findById(articleId).orElse(null);
        User user = userRepository.findByUsername(username).orElse(null);
        if (article == null || user == null) return false;
        return articleLikeRepository.existsByArticleAndUser(article, user);
    }
}
