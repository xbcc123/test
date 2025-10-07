package com.example.demo.service;

import com.example.demo.model.Video;
import com.example.demo.model.dto.VideoDTO;
import com.example.demo.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class VideoService {
    @Value("${app.video.storage:uploads/videos}")
    private String storageDir;

    @Autowired
    private VideoRepository videoRepository;

    private Path ensureDir() throws IOException {
        Path p = Path.of(storageDir);
        if (!Files.exists(p)) {
            Files.createDirectories(p);
        }
        return p;
    }

    @Transactional
    public VideoDTO upload(Long uploaderUserId, String title, String description, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }
        String original = file.getOriginalFilename();
        String ext = "";
        if (original != null && original.contains(".")) {
            ext = original.substring(original.lastIndexOf('.'));
        }
        String stored = UUID.randomUUID().toString().replaceAll("-", "") + ext;
        Path dir = ensureDir();
        Path target = dir.resolve(stored);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        Video v = new Video();
        v.setTitle(title == null || title.isBlank() ? (original == null ? stored : original) : title.trim());
        v.setDescription(description);
        v.setFilename(stored);
        v.setOriginalFilename(original);
        v.setSize(file.getSize());
        v.setContentType(file.getContentType());
        v.setUploaderUserId(uploaderUserId);
        v.setUploadTime(LocalDateTime.now());
        v = videoRepository.save(v);
        return toDTO(v);
    }

    public Page<VideoDTO> page(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return videoRepository.findAllByOrderByUploadTimeDesc(pageable).map(this::toDTO);
    }

    public VideoDTO get(Long id) {
        return videoRepository.findById(id).map(this::toDTO).orElse(null);
    }

    public Video getEntity(Long id) { // 新增：便于控制器获取原始实体
        return videoRepository.findById(id).orElse(null);
    }

    @Transactional
    public boolean delete(Long id) {
        return videoRepository.findById(id).map(v -> {
            videoRepository.deleteById(id);
            try {
                Path f = Path.of(storageDir).resolve(v.getFilename());
                Files.deleteIfExists(f);
            } catch (Exception ignored) {}
            return true;
        }).orElse(false);
    }

    private VideoDTO toDTO(Video v) {
        VideoDTO dto = new VideoDTO(v.getId(), v.getTitle(), v.getDescription(), v.getOriginalFilename(), v.getSize(), v.getContentType(), v.getUploaderUserId(), v.getUploadTime());
        dto.setFilename(v.getFilename());
        dto.setPlayUrl("/api/videos/" + v.getId() + "/stream");
        return dto;
    }
}
