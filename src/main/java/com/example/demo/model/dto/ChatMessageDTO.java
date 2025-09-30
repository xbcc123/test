package com.example.demo.model.dto;

import java.time.LocalDateTime;

public class ChatMessageDTO {
    private Long id;
    private Long fromUserId;
    private Long toUserId;
    private String content;
    private LocalDateTime createTime;
    private Boolean readFlag;
    private BasicUserDTO fromUser; // 新增
    private BasicUserDTO toUser;   // 新增

    public ChatMessageDTO() {}

    public ChatMessageDTO(Long id, Long fromUserId, Long toUserId, String content, LocalDateTime createTime, Boolean readFlag) {
        this.id = id;
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.content = content;
        this.createTime = createTime;
        this.readFlag = readFlag;
    }

    // 增加含用户信息的构造器
    public ChatMessageDTO(Long id, Long fromUserId, Long toUserId, String content, LocalDateTime createTime, Boolean readFlag,
                          BasicUserDTO fromUser, BasicUserDTO toUser) {
        this(id, fromUserId, toUserId, content, createTime, readFlag);
        this.fromUser = fromUser;
        this.toUser = toUser;
    }

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
    public BasicUserDTO getFromUser() { return fromUser; }
    public void setFromUser(BasicUserDTO fromUser) { this.fromUser = fromUser; }
    public BasicUserDTO getToUser() { return toUser; }
    public void setToUser(BasicUserDTO toUser) { this.toUser = toUser; }
}
