package com.example.demo.controller;

import com.example.demo.model.ApiResponse;
import com.example.demo.model.ErrorCode;
import com.example.demo.model.CustomUserDetails;
import com.example.demo.model.dto.ChatMessageDTO;
import com.example.demo.model.dto.ConversationDTO;
import com.example.demo.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@Tag(name = "即时通讯", description = "IM 聊天接口")
public class ChatController {

    @Autowired
    private ChatService chatService;

    private Long currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails cud) {
            return cud.getUserId();
        }
        return null;
    }

    @GetMapping("/conversations")
    @Operation(summary = "会话列表", description = "返回最近会话与未读数")
    public ApiResponse<List<ConversationDTO>> conversations() {
        Long uid = currentUserId();
        if (uid == null) return ApiResponse.error(ErrorCode.UNAUTHORIZED, "未登录");
        return ApiResponse.success(chatService.listConversations(uid));
    }

    @GetMapping("/messages")
    @Operation(summary = "查询消息", description = "分页拉取与某用户的聊天记录（倒序）")
    public ApiResponse<List<ChatMessageDTO>> messages(@RequestParam Long targetUserId,
                                                       @RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "20") int size,
                                                       @RequestParam(defaultValue = "true") boolean markRead) {
        Long uid = currentUserId();
        if (uid == null) return ApiResponse.error(ErrorCode.UNAUTHORIZED, "未登录");
        return ApiResponse.success(chatService.listMessages(uid, targetUserId, page, size, markRead));
    }

    @PostMapping("/send")
    @Operation(summary = "发送消息", description = "通过 HTTP 发送一条消息（WebSocket 不可用的降级方式）")
    public ApiResponse<ChatMessageDTO> send(@RequestBody Map<String, String> body) {
        Long uid = currentUserId();
        if (uid == null) return ApiResponse.error(ErrorCode.UNAUTHORIZED, "未登录");
        Long toUserId = null;
        try { toUserId = Long.valueOf(body.get("toUserId")); } catch (Exception ignored) {}
        String content = body.get("content");
        if (toUserId == null || toUserId <= 0 || content == null || content.isBlank()) {
            return ApiResponse.error(ErrorCode.PARAM_ERROR, "参数错误");
        }
        return ApiResponse.success(chatService.sendMessage(uid, toUserId, content));
    }

    @PostMapping("/read")
    @Operation(summary = "标记会话已读", description = "将与某用户的未读消息标记为已读")
    public ApiResponse<Void> markRead(@RequestBody Map<String, String> body) {
        Long uid = currentUserId();
        if (uid == null) return ApiResponse.error(ErrorCode.UNAUTHORIZED, "未登录");
        Long targetUserId = null;
        try { targetUserId = Long.valueOf(body.get("targetUserId")); } catch (Exception ignored) {}
        if (targetUserId == null || targetUserId <= 0) return ApiResponse.error(ErrorCode.PARAM_ERROR, "参数错误");
        chatService.markConversationRead(uid, targetUserId);
        return ApiResponse.success();
    }

    @GetMapping("/unread/total")
    @Operation(summary = "未读总数", description = "获取当前用户所有会话的未读消息总数")
    public ApiResponse<Long> unreadTotal() {
        Long uid = currentUserId();
        if (uid == null) return ApiResponse.error(ErrorCode.UNAUTHORIZED, "未登录");
        return ApiResponse.success(chatService.totalUnread(uid));
    }
}

