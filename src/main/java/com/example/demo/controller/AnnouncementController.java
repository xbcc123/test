package com.example.demo.controller;

import com.example.demo.model.dto.AnnouncementDTO;
import com.example.demo.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
public class AnnouncementController {
    @Autowired
    private AnnouncementService announcementService;

    // Public list (published only)
    @GetMapping("/api/announcements")
    public Page<AnnouncementDTO> publicList(@RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "10") int size,
                                            @RequestParam(required = false) String type,
                                            @RequestParam(required = false) String keyword) {
        return announcementService.publicList(page, size, type, keyword);
    }

    // Public detail
    @GetMapping("/api/announcements/{id}")
    public ResponseEntity<AnnouncementDTO> publicDetail(@PathVariable Long id) {
        AnnouncementDTO dto = announcementService.getPublicDetail(id);
        if (dto == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(dto);
    }

    // Admin list (all statuses)
    @GetMapping("/admin/announcements")
    public Page<AnnouncementDTO> adminList(@RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "10") int size,
                                           @RequestParam(required = false) String status,
                                           @RequestParam(required = false) String type,
                                           @RequestParam(required = false) String keyword) {
        return announcementService.adminList(page, size, status, type, keyword);
    }

    // Admin detail
    @GetMapping("/admin/announcements/{id}")
    public ResponseEntity<AnnouncementDTO> adminDetail(@PathVariable Long id) {
        AnnouncementDTO dto = announcementService.getAdminDetail(id);
        return dto == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(dto);
    }

    @PostMapping("/admin/announcements")
    public AnnouncementDTO create(@RequestBody AnnouncementDTO dto,
                                   @RequestParam(required = false) Long creatorId) {
        // If integrated with security, fetch current user ID instead of request param
        Long uid = creatorId == null ? 0L : creatorId;
        return announcementService.create(dto, uid);
    }

    @PutMapping("/admin/announcements/{id}")
    public ResponseEntity<AnnouncementDTO> update(@PathVariable Long id, @RequestBody AnnouncementDTO dto) {
        AnnouncementDTO updated = announcementService.update(id, dto);
        return updated == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(updated);
    }

    @DeleteMapping("/admin/announcements/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        boolean ok = announcementService.delete(id);
        return ok ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    @PutMapping("/admin/announcements/{id}/publish")
    public ResponseEntity<AnnouncementDTO> publish(@PathVariable Long id) {
        AnnouncementDTO dto = announcementService.publish(id);
        return dto == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(dto);
    }

    @PutMapping("/admin/announcements/{id}/unpublish")
    public ResponseEntity<AnnouncementDTO> unpublish(@PathVariable Long id) {
        AnnouncementDTO dto = announcementService.unpublish(id);
        return dto == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(dto);
    }
}

