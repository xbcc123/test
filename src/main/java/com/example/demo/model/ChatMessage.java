package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 点对点聊天消息实体
 */
@Entity
@Table(name = "chat_message", indexes = {
        @Index(name = "idx_conversation_key_time", columnList = "conversationKey,createTime"),
        @Index(name = "idx_to_user_read", columnList = "toUserId,readFlag")
})
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long fromUserId;
    private Long toUserId; // 暂时仅支持单聊（后续群聊可扩展 groupId 字段）
    @Column(length = 2000)
    private String content;
    private LocalDateTime createTime;
    private Boolean readFlag = false; // 是否已读
    private String conversationKey; // 参与双方 (minId_maxId)

    @PrePersist
    public void prePersist() {
        if (createTime == null) createTime = LocalDateTime.now();
        if (conversationKey == null && fromUserId != null && toUserId != null) {
            long a = Math.min(fromUserId, toUserId);
            long b = Math.max(fromUserId, toUserId);
            conversationKey = a + "_" + b;
        }
        if (readFlag == null) readFlag = false;
    }

    // getters / setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getFromUserId() { return fromUserId; }
    public void setFromUserId(Long fromUserId) { this.fromUserId = fromUserId; }
    public Long getToUserId() { return toUserId; }
    public void setToUserId(Long toUserId) { this.toUserId = toUserId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public Boolean getReadFlag() { return readFlag; }
    public void setReadFlag(Boolean readFlag) { this.readFlag = readFlag; }
    public String getConversationKey() { return conversationKey; }
    public void setConversationKey(String conversationKey) { this.conversationKey = conversationKey; }
}

