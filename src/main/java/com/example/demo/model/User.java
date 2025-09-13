package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class User {
    @Id
    private String username;
    private String password;
    private Integer score; // 积分
    private String role; // 达人称号
    private String favorites; // 收藏（逗号分隔的ID列表，简化实现）

    public User() {}

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getFavorites() { return favorites; }
    public void setFavorites(String favorites) { this.favorites = favorites; }
}
