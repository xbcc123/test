package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.model.dto.UserDTO;
import com.example.demo.model.Role;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.ArticleLikeRepository;
import com.example.demo.repository.RoleRepository;
import com.example.demo.model.dto.RoleSimpleDTO;
import com.example.demo.model.Department;
import com.example.demo.model.Position;
import com.example.demo.repository.DepartmentRepository;
import com.example.demo.repository.PositionRepository;
import com.example.demo.model.dto.DepartmentSimpleDTO;
import com.example.demo.model.dto.PositionSimpleDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ArticleLikeRepository articleLikeRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private PositionRepository positionRepository;

    public User validateUser(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent() && userOpt.get().getPassword() != null && userOpt.get().getPassword().equals(password)) {
            User user = userOpt.get();
//            user.setPassword(null); // 不返回密码
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

    public boolean assignDepartment(Long userId, Long departmentId) {
        var userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) return false;
        User user = userOpt.get();
        Department dept = departmentId == null ? null : departmentRepository.findById(departmentId).orElse(null);
        user.setDepartment(dept);
        userRepository.save(user);
        return true;
    }

    public boolean assignPositions(Long userId, Set<Long> positionIds) {
        var userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) return false;
        User user = userOpt.get();
        Set<Position> positions = positionIds == null ? java.util.Collections.emptySet() : new java.util.HashSet<>(positionRepository.findAllById(positionIds));
        user.setPositions(positions);
        userRepository.save(user);
        return true;
    }

    public UserDTO userToDTO(User user) {
        if (user == null) return null;
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setNickname(user.getNickname());
        dto.setAvatar(user.getAvatar());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setStatus(user.getStatus());
        dto.setScore(user.getScore());
        dto.setFavorites(user.getFavorites());
        if (user.getRoles() != null) {
            dto.setRoles(user.getRoles().stream()
                    .map(r -> new RoleSimpleDTO(r.getId(), r.getName(), r.getDescription()))
                    .collect(Collectors.toSet()));
        } else {
            dto.setRoles(java.util.Collections.emptySet());
        }
        if (user.getDepartment() != null) {
            var d = user.getDepartment();
            dto.setDepartment(new DepartmentSimpleDTO(d.getId(), d.getName(), d.getCode(), d.getDescription(), d.getParent() == null ? null : d.getParent().getId()));
        }
        if (user.getPositions() != null) {
            dto.setPositions(user.getPositions().stream()
                    .map(p -> new PositionSimpleDTO(p.getId(), p.getName(), p.getCode(), p.getDescription(), p.getDepartment()==null?null:p.getDepartment().getId()))
                    .collect(java.util.stream.Collectors.toSet()));
        } else {
            dto.setPositions(java.util.Collections.emptySet());
        }
        return dto;
    }
}
