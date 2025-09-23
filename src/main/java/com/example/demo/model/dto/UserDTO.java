package com.example.demo.model.dto;

import java.util.Set;

public class UserDTO {
    private Long id;
    private String username;
    private String nickname;
    private String avatar;
    private String email;
    private String phone;
    private String status;
    private Integer score;
    private String favorites;
    private Set<RoleSimpleDTO> roles;
    private DepartmentSimpleDTO department;
    private Set<PositionSimpleDTO> positions;
    // getter/setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }
    public String getFavorites() { return favorites; }
    public void setFavorites(String favorites) { this.favorites = favorites; }
    public Set<RoleSimpleDTO> getRoles() { return roles; }
    public void setRoles(Set<RoleSimpleDTO> roles) { this.roles = roles; }
    public DepartmentSimpleDTO getDepartment() { return department; }
    public void setDepartment(DepartmentSimpleDTO department) { this.department = department; }
    public Set<PositionSimpleDTO> getPositions() { return positions; }
    public void setPositions(Set<PositionSimpleDTO> positions) { this.positions = positions; }
}
