package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/users")
@Tag(name = "用户接口", description = "用户相关操作")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @GetMapping("/{username}")
    @Operation(summary = "获取用户", description = "根据用户名获取用户信息")
    public Map<String, Object> getUser(@PathVariable String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) return null;
        Map<String, Object> result = new HashMap<>();
        result.put("id", user.getId());
        result.put("username", user.getUsername());
        result.put("nickname", user.getNickname());
        result.put("avatar", user.getAvatar());
        result.put("email", user.getEmail());
        result.put("phone", user.getPhone());
        result.put("role", user.getRole());
        result.put("score", user.getScore());
        result.put("favorites", user.getFavorites());
        result.put("status", user.getStatus());
        result.put("likeCount", userService.getUserLikeCount(user.getId()));
        return result;
    }

    @PostMapping("/{username}/favorites")
    @Operation(summary = "更新收藏", description = "添加或移除用户收藏")
    public User updateFavorites(@PathVariable String username, @RequestParam String type, @RequestParam String id, @RequestParam boolean add) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) return null;
        String fav = user.getFavorites();
        Set<String> favSet = new HashSet<>();
        if (fav != null && !fav.isEmpty()) favSet.addAll(Arrays.asList(fav.split(",")));
        String key = type + ":" + id;
        if (add) favSet.add(key); else favSet.remove(key);
        user.setFavorites(String.join(",", favSet));
        return userRepository.save(user);
    }

    @GetMapping("/{username}/favorites")
    @Operation(summary = "获取收藏", description = "获取用户收藏列表")
    public List<String> getFavorites(@PathVariable String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null || user.getFavorites() == null) return Collections.emptyList();
        return Arrays.asList(user.getFavorites().split(","));
    }

    @PostMapping("/{username}/score")
    @Operation(summary = "更新积分", description = "增加或减少用户积分")
    public User updateScore(@PathVariable String username, @RequestParam int delta) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) return null;
        if (user.getScore() == null) user.setScore(0);
        user.setScore(user.getScore() + delta);
        return userRepository.save(user);
    }

    @PutMapping("/{username}")
    @Operation(summary = "更新用户", description = "根据用户名更新用户信息")
    public User updateUser(@PathVariable String username, @RequestBody User user) {
        user.setUsername(username);
        return userRepository.save(user);
    }

    @GetMapping("")
    @Operation(summary = "获取用户列表", description = "获取用户分页列表，可根据关键字搜索")
    public Page<User> getUsers(@RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "10") int size,
                              @RequestParam(required = false) String keyword) {
        Pageable pageable = PageRequest.of(page, size);
        if (keyword != null && !keyword.isEmpty()) {
            return userRepository.findByUsernameContainingOrNicknameContaining(keyword, keyword, pageable);
        } else {
            return userRepository.findAll(pageable);
        }
    }

    @PostMapping("")
    @Operation(summary = "创建用户", description = "添加新用户")
    public User createUser(@RequestBody User user) {
        return userRepository.save(user);
    }

    @DeleteMapping("/{username}")
    @Operation(summary = "删除用户", description = "根据用户名删除用户")
    public void deleteUser(@PathVariable String username) {
        userRepository.findByUsername(username).ifPresent(user -> userRepository.deleteById(user.getId()));
    }

    @PutMapping("/{username}/status")
    @Operation(summary = "更新用户状态", description = "修改用户的状态")
    public User updateStatus(@PathVariable String username, @RequestParam String status) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) return null;
        user.setStatus(status);
        return userRepository.save(user);
    }

    @PutMapping("/{username}/reset-password")
    @Operation(summary = "重置密码", description = "为用户设置新密码")
    public User resetPassword(@PathVariable String username, @RequestParam String newPassword) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) return null;
        user.setPassword(newPassword);
        return userRepository.save(user);
    }

}
