package com.example.demo.controller;

import com.example.demo.model.Article;
import com.example.demo.repository.ArticleRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/articles")
@Tag(name = "文章接口", description = "文章相关操作")
public class ArticleController {
    @Autowired
    private ArticleRepository articleRepository;

    @GetMapping
    @Operation(summary = "获取所有文章", description = "获取所有文章列表")
    public List<Article> getAll() {
        return articleRepository.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取文章", description = "根据ID获取单个文章详情")
    public Article getById(@PathVariable Long id) {
        return articleRepository.findById(id).orElse(null);
    }

    @PostMapping
    @Operation(summary = "创建文章", description = "创建一篇新文章")
    public Article create(@RequestBody Article article) {
        return articleRepository.save(article);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新文章", description = "根据ID更新文章内容")
    public Article update(@PathVariable Long id, @RequestBody Article article) {
        article.setId(id);
        return articleRepository.save(article);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除文章", description = "根据ID删除文章")
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
