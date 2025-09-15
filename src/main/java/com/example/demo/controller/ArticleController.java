package com.example.demo.controller;

import com.example.demo.model.Article;
import com.example.demo.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/articles")
public class ArticleController {
    @Autowired
    private ArticleRepository articleRepository;

    @GetMapping
    public List<Article> getAll() {
        return articleRepository.findAll();
    }

    @GetMapping("/{id}")
    public Article getById(@PathVariable Long id) {
        return articleRepository.findById(id).orElse(null);
    }

    @PostMapping
    public Article create(@RequestBody Article article) {
        return articleRepository.save(article);
    }

    @PutMapping("/{id}")
    public Article update(@PathVariable Long id, @RequestBody Article article) {
        article.setId(id);
        return articleRepository.save(article);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        articleRepository.deleteById(id);
    }

    @PostMapping("/testdata")
    public List<Article> generateTestData() {
        List<Article> list = new java.util.ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            Article a = new Article();
            a.setTitle("测试文章标题" + i);
            a.setContent("这是测试文章内容，编号：" + i + "，时间戳：" + System.currentTimeMillis());
            list.add(a);
        }
        return articleRepository.saveAll(list);
    }

    @GetMapping("/page")
    public Page<Article> getPage(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return articleRepository.findAll(pageable);
    }

    @GetMapping("/search")
    public Page<Article> search(@RequestParam String keyword,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return articleRepository.findByTitleContainingOrContentContaining(keyword, keyword, pageable);
    }
}
