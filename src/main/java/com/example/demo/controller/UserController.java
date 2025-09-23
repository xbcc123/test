package com.example.demo.controller;

import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.model.dto.UserDTO;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import com.example.demo.model.ApiResponse;
import com.example.demo.model.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<UserDTO> getUser(@PathVariable String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        UserDTO dto = userService.userToDTO(user);
        return ResponseEntity.ok(dto);
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
    public List<UserDTO> getUsers(@RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "10") int size,
                              @RequestParam(required = false) String keyword) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage;
        if (keyword != null && !keyword.isEmpty()) {
            userPage = userRepository.findByUsernameContainingOrNicknameContaining(keyword, keyword, pageable);
        } else {
            userPage = userRepository.findAll(pageable);
        }
        List<UserDTO> dtoList = userPage.getContent().stream().map(userService::userToDTO).toList();
        return dtoList;
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
    @Operation(summary = "更新用户状态", description = "修改用户的状态，支持 query 参数 或 JSON body {\"status\":\"...\"}")
    public ResponseEntity<ApiResponse<?>> updateStatus(@PathVariable String username,
                                                        @RequestParam(value = "status", required = false) String statusParam,
                                                        @RequestBody(required = false) Map<String, Object> body) {
        String newStatus = statusParam;
        if (newStatus == null && body != null) {
            Object v = body.get("status");
            if (v != null) newStatus = String.valueOf(v);
        }
        if (newStatus == null || newStatus.isBlank()) {
            return ResponseEntity.badRequest().body(ApiResponse.error(ErrorCode.VALIDATION_ERROR, "缺少 status 参数"));
        }
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(ErrorCode.NOT_FOUND, "用户不存在"));
        }
        user.setStatus(newStatus);
        userRepository.save(user);
        return ResponseEntity.ok(ApiResponse.success(userService.userToDTO(user), "更新成功"));
    }

    @PutMapping("/{username}/reset-password")
    @Operation(summary = "重置密码", description = "为用户设置新密码")
    public User resetPassword(@PathVariable String username, @RequestParam String newPassword) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) return null;
        user.setPassword(newPassword);
        return userRepository.save(user);
    }

    @PutMapping("/{userId}/roles")
    @Operation(summary = "分配角色", description = "为用户分配角色")
    public Map<String, Object> assignRoles(@PathVariable Long userId, @RequestBody Set<Long> roleIds) {
        boolean success = userService.assignRoles(userId, roleIds);
        return Map.of("success", success);
    }

    @PutMapping("/{userId}/department")
    @Operation(summary = "分配部门", description = "为用户设置所属部门，传 null 或不传可清除")
    public ResponseEntity<ApiResponse<?>> assignDepartment(@PathVariable Long userId,
                                                           @RequestBody(required = false) Map<String,Object> body,
                                                           @RequestParam(value = "departmentId", required = false) Long departmentIdParam) {
        Long deptId = departmentIdParam;
        if (deptId == null && body != null && body.get("departmentId") != null) {
            deptId = body.get("departmentId") == null ? null : Long.valueOf(body.get("departmentId").toString());
        }
        boolean ok = userService.assignDepartment(userId, deptId);
        if (!ok) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(ErrorCode.NOT_FOUND, "用户不存在"));
        var user = userRepository.findById(userId).orElse(null);
        return ResponseEntity.ok(ApiResponse.success(userService.userToDTO(user), "部门已更新"));
    }

    @PutMapping("/{userId}/positions")
    @Operation(summary = "分配岗位", description = "为用户批量设置岗位，覆盖式更新")
    public ResponseEntity<ApiResponse<?>> assignPositions(@PathVariable Long userId,
                                                           @RequestBody(required = false) Map<String,Object> body,
                                                           @RequestParam(value = "positionIds", required = false) List<Long> positionIdsParam) {
        java.util.Set<Long> ids = new java.util.HashSet<>();
        if (positionIdsParam != null) ids.addAll(positionIdsParam);
        if (body != null && body.get("positionIds") instanceof java.util.Collection<?> c) {
            for (Object o : c) { if (o != null) ids.add(Long.valueOf(o.toString())); }
        }
        boolean ok = userService.assignPositions(userId, ids);
        if (!ok) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(ErrorCode.NOT_FOUND, "用户不存在"));
        var user = userRepository.findById(userId).orElse(null);
        return ResponseEntity.ok(ApiResponse.success(userService.userToDTO(user), "岗位已更新"));
    }

}
