package com.example.demo;

import com.example.demo.model.dto.ChatMessageDTO;
import com.example.demo.model.dto.ConversationDTO;
import com.example.demo.service.ChatService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ChatServiceIntegrationTest {

    @Autowired
    private ChatService chatService;

    @Test
    void testSendAndQueryConversation() {
        ChatMessageDTO m1 = chatService.sendMessage(1L, 2L, "Hello 2");
        ChatMessageDTO m2 = chatService.sendMessage(2L, 1L, "Hi 1");
        Assertions.assertNotNull(m1.getId());
        Assertions.assertNotNull(m2.getId());
        Assertions.assertNotNull(m1.getFromUser());
        List<ConversationDTO> cons = chatService.listConversations(1L);
        Assertions.assertFalse(cons.isEmpty());
        ConversationDTO c = cons.get(0);
        Assertions.assertEquals(2L, c.getTargetUserId());
        Assertions.assertNotNull(c.getTargetUser());
        List<ChatMessageDTO> msgs = chatService.listMessages(1L, 2L,0,10,true);
        Assertions.assertTrue(msgs.size()>=2);
        Assertions.assertNotNull(msgs.get(0).getFromUser());
        long unreadAfter = chatService.totalUnread(1L);
        Assertions.assertEquals(0, unreadAfter);
    }
}
