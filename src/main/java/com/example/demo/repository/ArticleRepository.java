package com.example.demo.repository;

import com.example.demo.model.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    Page<Article> findByTitleContainingOrContentContaining(String title, String content, Pageable pageable);
}
