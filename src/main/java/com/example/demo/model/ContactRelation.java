package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "contact_relation", uniqueConstraints = {
        @UniqueConstraint(name = "uk_owner_contact", columnNames = {"ownerUserId","contactUserId"})
}, indexes = {
        @Index(name = "idx_owner", columnList = "ownerUserId")
})
public class ContactRelation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long ownerUserId; // 当前用户
    private Long contactUserId; // 联系人用户ID
    private String remark; // 备注名
    private Boolean blocked = false; // 是否拉黑
    private LocalDateTime createTime;

    @PrePersist
    public void prePersist(){
        if(createTime==null) createTime = LocalDateTime.now();
        if(blocked==null) blocked = false;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getOwnerUserId() { return ownerUserId; }
    public void setOwnerUserId(Long ownerUserId) { this.ownerUserId = ownerUserId; }
    public Long getContactUserId() { return contactUserId; }
    public void setContactUserId(Long contactUserId) { this.contactUserId = contactUserId; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public Boolean getBlocked() { return blocked; }
    public void setBlocked(Boolean blocked) { this.blocked = blocked; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}

