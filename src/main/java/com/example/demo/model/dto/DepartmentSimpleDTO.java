package com.example.demo.model.dto;

import java.util.List;

public class DepartmentSimpleDTO {
    private Long id;
    private String name;
    private String code;
    private String description;
    private Long parentId;
    private List<DepartmentSimpleDTO> children; // 新增: 树结构

    public DepartmentSimpleDTO() {}
    public DepartmentSimpleDTO(Long id, String name, String code, String description, Long parentId) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.description = description;
        this.parentId = parentId;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
    public List<DepartmentSimpleDTO> getChildren() { return children; }
    public void setChildren(List<DepartmentSimpleDTO> children) { this.children = children; }
}
