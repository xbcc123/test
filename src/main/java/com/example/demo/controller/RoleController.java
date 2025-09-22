package com.example.demo.controller;

import com.example.demo.model.Role;
import com.example.demo.model.dto.RoleCreateOrUpdateDTO;
import com.example.demo.model.dto.RoleDTO;
import com.example.demo.service.RoleService;

import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/roles")
public class RoleController {

    private static final Logger logger = LoggerFactory.getLogger(RoleController.class);

    @Autowired
    private RoleService roleService;

    @GetMapping
    public List<RoleDTO> getAll() {
        return roleService.findAll().stream().map(roleService::roleToDTO).toList();
    }

    @PutMapping("/{roleId}/permissions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> assignPermissions(@PathVariable @NotNull Long roleId, @RequestBody @Valid Set<Long> permissionIds) {
        try {
            boolean success = roleService.assignPermissions(roleId, permissionIds);
            logger.info("Permissions assigned to role {}: {}", roleId, permissionIds);
            return ResponseEntity.ok(Map.of("success", success));
        } catch (Exception e) {
            logger.error("Error assigning permissions to role {}: {}", roleId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to assign permissions"));
        }
    }

    @GetMapping("/{roleId}")
    public ResponseEntity<RoleDTO> getRole(@PathVariable @NotNull Long roleId) {
        Role role = roleService.findAll().stream().filter(r -> r.getId().equals(roleId)).findFirst().orElse(null);
        if (role == null) {
            logger.warn("Role not found: {}", roleId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(roleService.roleToDTO(role));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> createRole(@RequestBody @Valid Map<String, Object> roleData) {
        try {
            boolean success = roleService.createRole(roleData);
            if (!success) {
                logger.warn("Failed to create role: {}", roleData);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Failed to create role"));
            }
            logger.info("Role created successfully: {}", roleData);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("success", true));
        } catch (Exception e) {
            logger.error("Error creating role: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to create role"));
        }
    }

    @DeleteMapping("/{roleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteRole(@PathVariable @NotNull Long roleId) {
        try {
            boolean success = roleService.deleteRole(roleId);
            if (!success) {
                logger.warn("Role not found for deletion: {}", roleId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Role not found"));
            }
            logger.info("Role deleted successfully: {}", roleId);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            logger.error("Error deleting role {}: {}", roleId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to delete role"));
        }
    }

    @PutMapping("/{roleId}")
    public ResponseEntity<RoleDTO> updateRole(@PathVariable Long roleId, @RequestBody RoleCreateOrUpdateDTO dto) {
        dto.setId(roleId);
        Role updated = roleService.updateRole(dto);
        if (updated == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        return ResponseEntity.ok(roleService.roleToDTO(updated));
    }
}
