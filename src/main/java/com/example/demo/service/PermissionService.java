package com.example.demo.service;

import com.example.demo.model.Permission;
import com.example.demo.repository.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class PermissionService {
    @Autowired
    private PermissionRepository permissionRepository;

    public List<Permission> findAll() {
        return permissionRepository.findAll();
    }
    public Optional<Permission> findById(Long id) {
        return permissionRepository.findById(id);
    }
    public Permission save(Permission permission) {
        return permissionRepository.save(permission);
    }
    public void deleteById(Long id) {
        permissionRepository.deleteById(id);
    }
}

