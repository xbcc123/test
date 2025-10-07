package com.example.demo.model.dto;

import java.time.LocalDateTime;

public class VideoDTO {
    private Long id;
    private String title;
    private String description;
    private String originalFilename;
    private Long size;
    private String contentType;
    private Long uploaderUserId;
    private LocalDateTime uploadTime;
    private String filename; // 存储文件名
    private String playUrl;  // 播放URL

    public VideoDTO() {}
    public VideoDTO(Long id, String title, String description, String originalFilename, Long size, String contentType, Long uploaderUserId, LocalDateTime uploadTime) {
        this.id = id; this.title = title; this.description = description; this.originalFilename = originalFilename; this.size = size; this.contentType = contentType; this.uploaderUserId = uploaderUserId; this.uploadTime = uploadTime;
    }
    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }
    public String getPlayUrl() { return playUrl; }
    public void setPlayUrl(String playUrl) { this.playUrl = playUrl; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getOriginalFilename() { return originalFilename; }
    public void setOriginalFilename(String originalFilename) { this.originalFilename = originalFilename; }
    public Long getSize() { return size; }
    public void setSize(Long size) { this.size = size; }
    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    public Long getUploaderUserId() { return uploaderUserId; }
    public void setUploaderUserId(Long uploaderUserId) { this.uploaderUserId = uploaderUserId; }
    public LocalDateTime getUploadTime() { return uploadTime; }
    public void setUploadTime(LocalDateTime uploadTime) { this.uploadTime = uploadTime; }
}
