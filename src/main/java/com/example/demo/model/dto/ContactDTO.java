package com.example.demo.model.dto;

import java.time.LocalDateTime;

public class ContactDTO {
    private Long contactUserId;
    private String remark;
    private Boolean blocked;
    private LocalDateTime createTime;
    private BasicUserDTO user; // 联系人基础信息

    public ContactDTO() {}
    public ContactDTO(Long contactUserId, String remark, Boolean blocked, LocalDateTime createTime, BasicUserDTO user) {
        this.contactUserId = contactUserId;
        this.remark = remark;
        this.blocked = blocked;
        this.createTime = createTime;
        this.user = user;
    }
    public Long getContactUserId() { return contactUserId; }
    public void setContactUserId(Long contactUserId) { this.contactUserId = contactUserId; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public Boolean getBlocked() { return blocked; }
    public void setBlocked(Boolean blocked) { this.blocked = blocked; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public BasicUserDTO getUser() { return user; }
    public void setUser(BasicUserDTO user) { this.user = user; }
}

