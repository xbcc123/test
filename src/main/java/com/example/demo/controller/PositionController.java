package com.example.demo.controller;

import com.example.demo.model.dto.PositionSimpleDTO;
import com.example.demo.service.PositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/positions")
public class PositionController {
    @Autowired
    private PositionService positionService;

    @GetMapping
    public List<PositionSimpleDTO> list(@RequestParam(required = false) Long departmentId) {
        return positionService.listAll(departmentId);
    }

    @PostMapping
    public PositionSimpleDTO create(@RequestBody PositionSimpleDTO dto) { return positionService.create(dto); }

    @PutMapping("/{id}")
    public ResponseEntity<PositionSimpleDTO> update(@PathVariable Long id, @RequestBody PositionSimpleDTO dto) {
        PositionSimpleDTO updated = positionService.update(id, dto);
        return updated == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        boolean ok = positionService.delete(id);
        return ok ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}

