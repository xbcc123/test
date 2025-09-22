package com.example.demo.model;

import java.util.List;

public class PermissionDTO {
    private Long id;
    private String name;
    private String description;
    private Long parentId;
    private List<PermissionDTO> children;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
    public List<PermissionDTO> getChildren() { return children; }
    public void setChildren(List<PermissionDTO> children) { this.children = children; }
}

