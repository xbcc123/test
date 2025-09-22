package com.example.demo.controller;

import com.example.demo.model.Permission;
import com.example.demo.model.dto.PermissionCreateOrUpdateDTO;
import com.example.demo.model.dto.PermissionDTO;
import com.example.demo.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/permissions")
public class PermissionController {
    @Autowired
    private PermissionService permissionService;

    @GetMapping
    public List<PermissionDTO> getAllPermissions(@RequestParam(required = false) String keyword) {
        return permissionService.findAllAsTree(keyword);
    }

    @GetMapping("/{id}")
    public PermissionDTO getPermission(@PathVariable Long id) {
        return permissionService.findDTOById(id);
    }

    @PostMapping
    public Permission createPermission(@RequestBody PermissionCreateOrUpdateDTO dto) {
        return permissionService.createOrUpdate(dto);
    }

    @PutMapping("/{id}")
    public Permission updatePermission(@PathVariable Long id, @RequestBody PermissionCreateOrUpdateDTO dto) {
        dto.setId(id);
        return permissionService.createOrUpdate(dto);
    }

    @DeleteMapping("/{id}")
    public void deletePermission(@PathVariable Long id) {
        permissionService.deleteById(id);
    }
}
