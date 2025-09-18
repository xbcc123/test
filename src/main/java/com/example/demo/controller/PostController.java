package com.example.demo.controller;

import com.example.demo.model.CustomUserDetails;
import com.example.demo.model.Post;
import com.example.demo.repository.PostRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
@Tag(name = "帖子接口", description = "帖子相关操作")
public class PostController {
    @Autowired
    private PostRepository postRepository;

    @GetMapping
    @Operation(summary = "获取所有帖子", description = "获取所有帖子列表")
    public List<Post> getAll() {
        return postRepository.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取帖子", description = "根据ID获取单个帖子详情")
    public Post getById(@PathVariable Long id) {
        return postRepository.findById(id).orElse(null);
    }

    @PostMapping
    @Operation(summary = "创建帖子", description = "创建一条新帖子")
    public Post create(@RequestBody Post post, @AuthenticationPrincipal CustomUserDetails userDetails) {
        post.setUserId(userDetails.getUserId());
        post.setUsername(userDetails.getUsername());
        return postRepository.save(post);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新帖子", description = "根据ID更新帖子内容")
    public Post update(@PathVariable Long id, @RequestBody Post post) {
        post.setId(id);
        return postRepository.save(post);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除帖子", description = "根据ID删除帖子")
    public void delete(@PathVariable Long id) {
        postRepository.deleteById(id);
    }

    @PostMapping("/testdata")
    @Operation(summary = "生成测试数据", description = "批量生成测试用的帖子数据")
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
    @Operation(summary = "获取帖子分页", description = "获取帖子列表的分页数据")
    public Page<Post> getPage(@RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return postRepository.findAll(pageable);
    }

    @GetMapping("/search")
    @Operation(summary = "搜索帖子", description = "根据关键词搜索帖子")
    public Page<Post> search(@RequestParam String keyword,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return postRepository.findByContentContaining(keyword, pageable);
    }
}
