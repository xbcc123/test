package com.example.demo.model.dto;

import java.time.LocalDateTime;

public class ConversationDTO {
    private Long targetUserId; // 对方用户ID（兼容旧前端）
    private BasicUserDTO targetUser; // 对方用户基础信息
    private String lastContent;
    private LocalDateTime lastTime;
    private long unreadCount; // 当前登录用户对该会话的未读数

    public ConversationDTO() {}

    public ConversationDTO(Long targetUserId, String lastContent, LocalDateTime lastTime, long unreadCount) {
        this.targetUserId = targetUserId;
        this.lastContent = lastContent;
        this.lastTime = lastTime;
        this.unreadCount = unreadCount;
    }

    // 新构造函数，包含用户信息
    public ConversationDTO(Long targetUserId, BasicUserDTO targetUser, String lastContent, LocalDateTime lastTime, long unreadCount) {
        this.targetUserId = targetUserId;
        this.targetUser = targetUser;
        this.lastContent = lastContent;
        this.lastTime = lastTime;
        this.unreadCount = unreadCount;
    }

    public Long getTargetUserId() { return targetUserId; }
    public void setTargetUserId(Long targetUserId) { this.targetUserId = targetUserId; }
    public BasicUserDTO getTargetUser() { return targetUser; }
    public void setTargetUser(BasicUserDTO targetUser) { this.targetUser = targetUser; }
    public String getLastContent() { return lastContent; }
    public void setLastContent(String lastContent) { this.lastContent = lastContent; }
    public LocalDateTime getLastTime() { return lastTime; }
    public void setLastTime(LocalDateTime lastTime) { this.lastTime = lastTime; }
    public long getUnreadCount() { return unreadCount; }
    public void setUnreadCount(long unreadCount) { this.unreadCount = unreadCount; }
}
