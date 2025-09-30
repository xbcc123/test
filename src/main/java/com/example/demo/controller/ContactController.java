package com.example.demo.controller;

import com.example.demo.model.ApiResponse;
import com.example.demo.model.CustomUserDetails;
import com.example.demo.model.ErrorCode;
import com.example.demo.model.dto.ContactDTO;
import com.example.demo.service.ContactService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/contacts")
@Tag(name = "通讯录", description = "联系人管理接口")
public class ContactController {
    @Autowired
    private ContactService contactService;

    private Long currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails cud) {
            return cud.getUserId();
        }
        return null;
    }

    @GetMapping
    @Operation(summary = "联系人列表", description = "获取当前用户的全部联系人")
    public ApiResponse<List<ContactDTO>> list() {
        Long uid = currentUserId();
        if (uid == null) return ApiResponse.error(ErrorCode.UNAUTHORIZED, "未登录");
        return ApiResponse.success(contactService.list(uid));
    }

    @PostMapping
    @Operation(summary = "添加联系人", description = "添加一个联系人，可选备注")
    public ApiResponse<ContactDTO> add(@RequestBody Map<String,Object> body) {
        Long uid = currentUserId();
        if (uid == null) return ApiResponse.error(ErrorCode.UNAUTHORIZED, "未登录");
        Long contactUserId = body.get("contactUserId") == null ? null : Long.valueOf(body.get("contactUserId").toString());
        String remark = body.get("remark") == null ? null : body.get("remark").toString();
        if (contactUserId == null || contactUserId <= 0) return ApiResponse.error(ErrorCode.PARAM_ERROR, "contactUserId 必填");
        try {
            return ApiResponse.success(contactService.add(uid, contactUserId, remark));
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(ErrorCode.BUSINESS_ERROR, e.getMessage());
        }
    }

    @PutMapping("/{contactUserId}/remark")
    @Operation(summary = "修改备注", description = "更新联系人备注")
    public ApiResponse<ContactDTO> remark(@PathVariable Long contactUserId, @RequestBody Map<String,Object> body) {
        Long uid = currentUserId();
        if (uid == null) return ApiResponse.error(ErrorCode.UNAUTHORIZED, "未登录");
        String remark = body.get("remark") == null ? null : body.get("remark").toString();
        try {
            return ApiResponse.success(contactService.updateRemark(uid, contactUserId, remark));
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(ErrorCode.NOT_FOUND, "联系人不存在");
        }
    }

    @PutMapping("/{contactUserId}/block")
    @Operation(summary = "设置拉黑状态", description = "blocked=true 拉黑，false 取消")
    public ApiResponse<ContactDTO> block(@PathVariable Long contactUserId, @RequestParam boolean blocked) {
        Long uid = currentUserId();
        if (uid == null) return ApiResponse.error(ErrorCode.UNAUTHORIZED, "未登录");
        try {
            return ApiResponse.success(contactService.setBlocked(uid, contactUserId, blocked));
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(ErrorCode.NOT_FOUND, "联系人不存在");
        }
    }

    @DeleteMapping("/{contactUserId}")
    @Operation(summary = "删除联系人", description = "移除一条联系人关系")
    public ApiResponse<Void> delete(@PathVariable Long contactUserId) {
        Long uid = currentUserId();
        if (uid == null) return ApiResponse.error(ErrorCode.UNAUTHORIZED, "未登录");
        contactService.remove(uid, contactUserId);
        return ApiResponse.success();
    }
}

