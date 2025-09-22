package com.example.demo.service;

import com.example.demo.model.Permission;
import com.example.demo.model.Role;
import com.example.demo.model.RoleDTO;
import com.example.demo.repository.PermissionRepository;
import com.example.demo.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

    public Map<String, Object> getRoleDetails(Long roleId) {
        Optional<Role> roleOptional = roleRepository.findById(roleId);
        if (roleOptional.isEmpty()) {
            return null;
        }
        Role role = roleOptional.get();
        Map<String, Object> roleDetails = new HashMap<>();
        roleDetails.put("id", role.getId());
        roleDetails.put("name", role.getName());
        roleDetails.put("permissions", role.getPermissions());
        return roleDetails;
    }

    public boolean createRole(Map<String, Object> roleData) {
        if (!roleData.containsKey("name")) {
            return false;
        }
        String roleName = (String) roleData.get("name");
        String description = (String) roleData.getOrDefault("description", "");
        Role role = new Role();
        role.setName(roleName);
        role.setDescription(description);
        roleRepository.save(role);
        return true;
    }

    public boolean deleteRole(Long roleId) {
        Optional<Role> roleOptional = roleRepository.findById(roleId);
        if (roleOptional.isPresent()) {
            roleRepository.delete(roleOptional.get());
            return true;
        }
        return false;
    }

    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    public RoleDTO roleToDTO(Role role) {
        if (role == null) return null;
        RoleDTO dto = new RoleDTO();
        dto.setId(role.getId());
        dto.setName(role.getName());
        dto.setDescription(role.getDescription());
        if (role.getPermissions() != null) {
            dto.setPermissionIds(role.getPermissions().stream().map(Permission::getId).collect(Collectors.toSet()));
        }
        return dto;
    }
}
