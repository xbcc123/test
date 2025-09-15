package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/{username}")
    public User getUser(@PathVariable String username) {
        return userRepository.findById(username).orElse(null);
    }

    @PostMapping("/{username}/favorites")
    public User updateFavorites(@PathVariable String username, @RequestParam String type, @RequestParam String id, @RequestParam boolean add) {
        User user = userRepository.findById(username).orElse(null);
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
    public List<String> getFavorites(@PathVariable String username) {
        User user = userRepository.findById(username).orElse(null);
        if (user == null || user.getFavorites() == null) return Collections.emptyList();
        return Arrays.asList(user.getFavorites().split(","));
    }

    @PostMapping("/{username}/score")
    public User updateScore(@PathVariable String username, @RequestParam int delta) {
        User user = userRepository.findById(username).orElse(null);
        if (user == null) return null;
        if (user.getScore() == null) user.setScore(0);
        user.setScore(user.getScore() + delta);
        return userRepository.save(user);
    }

    @PutMapping("/{username}")
    public User updateUser(@PathVariable String username, @RequestBody User user) {
        user.setUsername(username);
        return userRepository.save(user);
    }

    @GetMapping("")
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
    public User createUser(@RequestBody User user) {
        return userRepository.save(user);
    }

    @DeleteMapping("/{username}")
    public void deleteUser(@PathVariable String username) {
        userRepository.deleteById(username);
    }

    @PutMapping("/{username}/status")
    public User updateStatus(@PathVariable String username, @RequestParam String status) {
        User user = userRepository.findById(username).orElse(null);
        if (user == null) return null;
        user.setStatus(status);
        return userRepository.save(user);
    }

    @PutMapping("/{username}/reset-password")
    public User resetPassword(@PathVariable String username, @RequestParam String newPassword) {
        User user = userRepository.findById(username).orElse(null);
        if (user == null) return null;
        user.setPassword(newPassword);
        return userRepository.save(user);
    }

}
