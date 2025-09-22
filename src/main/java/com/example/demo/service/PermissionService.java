package com.example.demo.service;

import com.example.demo.model.Permission;
import com.example.demo.model.dto.PermissionCreateOrUpdateDTO;
import com.example.demo.model.dto.PermissionDTO;
import com.example.demo.repository.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PermissionService {
    @Autowired
    private PermissionRepository permissionRepository;

    // 查询所有权限（扁平列表）
    public List<Permission> findAll() {
        return permissionRepository.findAll();
    }

    // 根据ID查询单个权限
    public Optional<Permission> findById(Long id) {
        return permissionRepository.findById(id);
    }

    // 新增或更新权限
    public Permission save(Permission permission) {
        return permissionRepository.save(permission);
    }

    // 根据ID删除权限
    public void deleteById(Long id) {
        permissionRepository.deleteById(id);
    }

    // 查询所有权限，并组装为树状结构（只返回根节点及其递归子节点，返回DTO避免无限递归）
    public List<PermissionDTO> findAllAsTree() {
        List<Permission> all = permissionRepository.findAll();
        List<Permission> roots = all.stream().filter(p -> p.getParent() == null).collect(Collectors.toList());
        List<PermissionDTO> rootDTOs = new ArrayList<>();
        for (Permission root : roots) {
            rootDTOs.add(permissionToDTO(root, all));
        }
        return rootDTOs;
    }

    // 根据ID查询单个权限并返回DTO
    public PermissionDTO findDTOById(Long id) {
        List<Permission> all = permissionRepository.findAll();
        Permission entity = all.stream().filter(p -> p.getId().equals(id)).findFirst().orElse(null);
        if (entity == null) return null;
        return permissionToDTO(entity, all);
    }

    // 实体转DTO递归方法
    private PermissionDTO permissionToDTO(Permission entity, List<Permission> all) {
        PermissionDTO dto = new PermissionDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setParentId(entity.getParent() == null ? null : entity.getParent().getId());
        // 递归设置children
        List<PermissionDTO> children = all.stream()
                .filter(p -> p.getParent() != null && p.getParent().getId().equals(entity.getId()))
                .map(child -> permissionToDTO(child, all))
                .collect(Collectors.toList());
        dto.setChildren(children);
        return dto;
    }

    // 新增或更新权限（只处理 parent，不处理 children，避免 orphanRemoval 异常）
    public Permission createOrUpdate(PermissionCreateOrUpdateDTO dto) {
        Permission permission = dto.getId() != null ?
            permissionRepository.findById(dto.getId()).orElse(new Permission()) : new Permission();
        permission.setName(dto.getName());
        permission.setDescription(dto.getDescription());
        if (dto.getParentId() != null) {
            Permission parent = permissionRepository.findById(dto.getParentId()).orElse(null);
            permission.setParent(parent);
        } else {
            permission.setParent(null);
        }
        // 不处理 children
        return permissionRepository.save(permission);
    }

    // 查询所有权限，并组装为树状结构，支持按名称模糊搜索
    public List<PermissionDTO> findAllAsTree(String keyword) {
        List<Permission> all = permissionRepository.findAll();
        List<Permission> roots = all.stream().filter(p -> p.getParent() == null).collect(Collectors.toList());
        List<PermissionDTO> rootDTOs = new ArrayList<>();
        for (Permission root : roots) {
            PermissionDTO dto = permissionToDTOWithFilter(root, all, keyword);
            if (dto != null) rootDTOs.add(dto);
        }
        return rootDTOs;
    }

    // 递归过滤 name 包含 keyword 的节点及其父节点
    private PermissionDTO permissionToDTOWithFilter(Permission entity, List<Permission> all, String keyword) {
        boolean match = (keyword == null || keyword.isEmpty()) || (entity.getName() != null && entity.getName().contains(keyword));
        List<PermissionDTO> children = all.stream()
                .filter(p -> p.getParent() != null && p.getParent().getId().equals(entity.getId()))
                .map(child -> permissionToDTOWithFilter(child, all, keyword))
                .filter(childDto -> childDto != null)
                .collect(Collectors.toList());
        if (match || !children.isEmpty()) {
            PermissionDTO dto = new PermissionDTO();
            dto.setId(entity.getId());
            dto.setName(entity.getName());
            dto.setDescription(entity.getDescription());
            dto.setParentId(entity.getParent() == null ? null : entity.getParent().getId());
            dto.setChildren(children);
            return dto;
        }
        return null;
    }
}
