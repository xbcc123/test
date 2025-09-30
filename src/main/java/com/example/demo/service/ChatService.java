package com.example.demo.service;

import com.example.demo.model.ChatMessage;
import com.example.demo.model.dto.ChatMessageDTO;
import com.example.demo.model.dto.ConversationDTO;
import com.example.demo.model.dto.BasicUserDTO;
import com.example.demo.repository.ChatMessageRepository;
import com.example.demo.repository.ContactRelationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService {
    @Autowired
    private ChatMessageRepository chatMessageRepository;
    @Autowired
    private UserCacheService userCacheService;
    @Autowired
    private ContactRelationRepository contactRelationRepository;

    public String buildConversationKey(Long a, Long b) {
        long x = Math.min(a, b);
        long y = Math.max(a, b);
        return x + "_" + y;
    }

    @Transactional
    public ChatMessageDTO sendMessage(Long fromUserId, Long toUserId, String content) {
        // 黑名单校验：任一方向存在 blocked=true 则禁止
        boolean blocked = contactRelationRepository
                .findByOwnerUserIdAndContactUserId(fromUserId, toUserId)
                .map(r -> Boolean.TRUE.equals(r.getBlocked())).orElse(false)
            || contactRelationRepository
                .findByOwnerUserIdAndContactUserId(toUserId, fromUserId)
                .map(r -> Boolean.TRUE.equals(r.getBlocked())).orElse(false);
        if (blocked) {
            throw new IllegalStateException("BLOCKED");
        }
        ChatMessage m = new ChatMessage();
        m.setFromUserId(fromUserId);
        m.setToUserId(toUserId);
        m.setContent(content);
        m.setCreateTime(LocalDateTime.now());
        String key = buildConversationKey(fromUserId, toUserId);
        m.setConversationKey(key);
        ChatMessage saved = chatMessageRepository.save(m);
        return toDTO(saved);
    }

    public List<ConversationDTO> listConversations(Long userId) {
        List<String> keys = chatMessageRepository.findConversationKeysByUser(userId);
        List<ConversationDTO> result = new ArrayList<>();
        for (String key : keys) {
            ChatMessage last = chatMessageRepository.findTop1ByConversationKeyOrderByCreateTimeDesc(key);
            if (last == null) continue;
            String[] parts = key.split("_");
            Long a = Long.valueOf(parts[0]);
            Long b = Long.valueOf(parts[1]);
            Long target = a.equals(userId) ? b : a;
            long unread = chatMessageRepository.countByConversationKeyAndToUserIdAndReadFlagFalse(key, userId);
            BasicUserDTO targetUser = userCacheService.getBasicUser(target);
            result.add(new ConversationDTO(target, targetUser, last.getContent(), last.getCreateTime(), unread));
        }
        return result;
    }

    @Transactional
    public List<ChatMessageDTO> listMessages(Long userId, Long targetUserId, int page, int size, boolean markRead) {
        String key = buildConversationKey(userId, targetUserId);
        Page<ChatMessage> p = chatMessageRepository.findByConversationKeyOrderByCreateTimeDesc(key, PageRequest.of(page, size));
        List<ChatMessage> content = p.getContent();
        // 倒序返回（前端按时间正序显示可 reverse）
        List<ChatMessageDTO> dtos = content.stream().map(this::toDTO).collect(Collectors.toList());
        if (markRead) {
            chatMessageRepository.markConversationRead(key, userId);
        }
        return dtos;
    }

    @Transactional
    public void markConversationRead(Long userId, Long targetUserId) {
        String key = buildConversationKey(userId, targetUserId);
        chatMessageRepository.markConversationRead(key, userId);
    }

    public long totalUnread(Long userId) {
        return chatMessageRepository.countByToUserIdAndReadFlagFalse(userId);
    }

    private ChatMessageDTO toDTO(ChatMessage m) {
        BasicUserDTO from = userCacheService.getBasicUser(m.getFromUserId());
        BasicUserDTO to = userCacheService.getBasicUser(m.getToUserId());
        return new ChatMessageDTO(m.getId(), m.getFromUserId(), m.getToUserId(), m.getContent(), m.getCreateTime(), m.getReadFlag(), from, to);
    }
}
