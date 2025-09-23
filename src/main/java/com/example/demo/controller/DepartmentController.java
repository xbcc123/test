package com.example.demo.controller;

import com.example.demo.model.dto.DepartmentSimpleDTO;
import com.example.demo.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/departments")
public class DepartmentController {
    @Autowired
    private DepartmentService departmentService;

    @GetMapping
    public List<DepartmentSimpleDTO> listAll() { return departmentService.listAll(); }

    @GetMapping("/root")
    public List<DepartmentSimpleDTO> listRoot() { return departmentService.listRoot(); }

    @GetMapping("/{parentId}/children")
    public List<DepartmentSimpleDTO> listChildren(@PathVariable Long parentId) { return departmentService.listChildren(parentId); }

    @PostMapping
    public DepartmentSimpleDTO create(@RequestBody DepartmentSimpleDTO dto) { return departmentService.create(dto); }

    @PutMapping("/{id}")
    public ResponseEntity<DepartmentSimpleDTO> update(@PathVariable Long id, @RequestBody DepartmentSimpleDTO dto) {
        DepartmentSimpleDTO updated = departmentService.update(id, dto);
        return updated == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        boolean ok = departmentService.delete(id);
        return ok ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}

