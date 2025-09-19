package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.model.Role;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.ArticleLikeRepository;
import com.example.demo.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ArticleLikeRepository articleLikeRepository;

    @Autowired
    private RoleRepository roleRepository;

    public User validateUser(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent() && userOpt.get().getPassword() != null && userOpt.get().getPassword().equals(password)) {
            User user = userOpt.get();
            user.setPassword(null); // 不返回密码
            return user;
        }
        return null;
    }

    public boolean registerUser(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) return false;
        user.setScore(0);
        user.setStatus("正常");
        userRepository.save(user);
        return true;
    }

    public boolean resetPassword(String username, String newPassword) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setPassword(newPassword);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    public boolean updateUserInfo(User user) {
        if (!userRepository.existsById(user.getId())) return false;
        userRepository.save(user);
        return true;
    }

    public long getUserLikeCount(Long userId) {
        return articleLikeRepository.countLikesByUserId(userId);
    }

    public boolean assignRoles(Long userId, Set<Long> roleIds) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) return false;
        User user = userOpt.get();
        Set<Role> roles = new java.util.HashSet<>(roleRepository.findAllById(roleIds));
        user.setRoles(roles);
        userRepository.save(user);
        return true;
    }
}
