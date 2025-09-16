package com.example.demo.controller;

import com.example.demo.model.Encyclopedia;
import com.example.demo.repository.EncyclopediaRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/encyclopedias")
@Tag(name = "百科接口", description = "百科相关操作")
public class EncyclopediaController {
    @Autowired
    private EncyclopediaRepository encyclopediaRepository;

    @GetMapping
    @Operation(summary = "获取所有百科", description = "获取所有百科条目列表")
    public List<Encyclopedia> getAll() {
        return encyclopediaRepository.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取百科", description = "根据ID获取单个百科详情")
    public Encyclopedia getById(@PathVariable Long id) {
        return encyclopediaRepository.findById(id).orElse(null);
    }

    @PostMapping
    @Operation(summary = "创建百科", description = "创建一条新百科")
    public Encyclopedia create(@RequestBody Encyclopedia encyclopedia) {
        return encyclopediaRepository.save(encyclopedia);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新百科", description = "根据ID更新百科内容")
    public Encyclopedia update(@PathVariable Long id, @RequestBody Encyclopedia encyclopedia) {
        encyclopedia.setId(id);
        return encyclopediaRepository.save(encyclopedia);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除百科", description = "根据ID删除百科")
    public void delete(@PathVariable Long id) {
        encyclopediaRepository.deleteById(id);
    }

    @PostMapping("/testdata")
    public List<Encyclopedia> generateTestData() {
        List<Encyclopedia> list = new java.util.ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            Encyclopedia e = new Encyclopedia();
            e.setTitle("测试百科条目" + i);
            e.setType("类型" + (i % 5 + 1));
            e.setContent("这是测试百科内容，编号：" + i + "，时间戳：" + System.currentTimeMillis());
            list.add(e);
        }
        return encyclopediaRepository.saveAll(list);
    }

    @GetMapping("/page")
    public Page<Encyclopedia> getPage(@RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return encyclopediaRepository.findAll(pageable);
    }

    @GetMapping("/search")
    public Page<Encyclopedia> search(@RequestParam String keyword,
                                     @RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return encyclopediaRepository.findByTitleContainingOrContentContaining(keyword, keyword, pageable);
    }
}
