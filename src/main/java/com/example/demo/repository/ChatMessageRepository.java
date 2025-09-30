package com.example.demo.repository;

import com.example.demo.model.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    Page<ChatMessage> findByConversationKeyOrderByCreateTimeDesc(String conversationKey, Pageable pageable);

    long countByToUserIdAndReadFlagFalse(Long toUserId);

    List<ChatMessage> findByToUserIdAndReadFlagFalse(Long toUserId);

    ChatMessage findTop1ByConversationKeyOrderByCreateTimeDesc(String conversationKey);

    long countByConversationKeyAndToUserIdAndReadFlagFalse(String conversationKey, Long toUserId);

    @Query("select m.conversationKey from ChatMessage m where (m.fromUserId = :userId or m.toUserId = :userId) group by m.conversationKey order by max(m.createTime) desc")
    List<String> findConversationKeysByUser(Long userId);

    @Transactional
    @Modifying
    @Query("update ChatMessage m set m.readFlag = true where m.conversationKey = :conversationKey and m.toUserId = :userId and m.readFlag = false")
    int markConversationRead(String conversationKey, Long userId);
}
