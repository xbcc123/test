package com.example.demo.service;

import com.example.demo.model.Permission;
import com.example.demo.model.Role;
import com.example.demo.repository.PermissionRepository;
import com.example.demo.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    public boolean assignPermissions(Long roleId, Set<Long> permissionIds) {
        Optional<Role> roleOpt = roleRepository.findById(roleId);
        if (roleOpt.isEmpty()) return false;
        Role role = roleOpt.get();
        Set<Permission> permissions = new java.util.HashSet<>(permissionRepository.findAllById(permissionIds));
        role.setPermissions(permissions);
        roleRepository.save(role);
        return true;
    }
}
