package com.example.demo.controller;

import com.example.demo.model.Post;
import com.example.demo.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {
    @Autowired
    private PostRepository postRepository;

    @GetMapping
    public List<Post> getAll() {
        return postRepository.findAll();
    }

    @GetMapping("/{id}")
    public Post getById(@PathVariable Long id) {
        return postRepository.findById(id).orElse(null);
    }

    @PostMapping
    public Post create(@RequestBody Post post) {
        return postRepository.save(post);
    }

    @PutMapping("/{id}")
    public Post update(@PathVariable Long id, @RequestBody Post post) {
        post.setId(id);
        return postRepository.save(post);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        postRepository.deleteById(id);
    }

    @PostMapping("/testdata")
    public List<Post> generateTestData() {
        List<Post> list = new java.util.ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            Post p = new Post();
            p.setUserId((long)(i % 20 + 1));
            p.setType("类型" + (i % 4 + 1));
            p.setContent("这是测试社区互动内容，编号：" + i + "，时间戳：" + System.currentTimeMillis());
            list.add(p);
        }
        return postRepository.saveAll(list);
    }

    @GetMapping("/page")
    public Page<Post> getPage(@RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return postRepository.findAll(pageable);
    }

    @GetMapping("/search")
    public Page<Post> search(@RequestParam String keyword,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return postRepository.findByContentContaining(keyword, pageable);
    }
}
