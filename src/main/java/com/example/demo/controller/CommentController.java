package com.example.demo.controller;

import com.example.demo.model.Comment;
import com.example.demo.repository.CommentRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
@Tag(name = "评论接口", description = "评论相关操作")
public class CommentController {
    @Autowired
    private CommentRepository commentRepository;

    @GetMapping
    @Operation(summary = "获取评论列表", description = "根据帖子ID获取评论列表")
    public List<Comment> getByPostId(@RequestParam Long postId) {
        return commentRepository.findByPostIdOrderByCreateTimeAsc(postId);
    }

    @PostMapping
    @Operation(summary = "创建评论", description = "添加一条新评论")
    public Comment create(@RequestBody Comment comment) {
        return commentRepository.save(comment);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除评论", description = "根据ID删除评论")
    public void delete(@PathVariable Long id) {
        commentRepository.deleteById(id);
    }
}
