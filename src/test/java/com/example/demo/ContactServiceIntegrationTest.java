package com.example.demo;

import com.example.demo.service.ChatService;
import com.example.demo.service.ContactService;
import com.example.demo.model.dto.ContactDTO;
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
public class ContactServiceIntegrationTest {

    @Autowired
    private ContactService contactService;
    @Autowired
    private ChatService chatService;

    @Test
    void testAddRemarkBlockUnblockAndDelete() {
        // 初始列表为空
        List<ContactDTO> list0 = contactService.list(1L);
        int baseSize = list0.size();

        // 添加联系人 2
        ContactDTO c = contactService.add(1L, 2L, "好友2");
        Assertions.assertEquals(2L, c.getContactUserId());
        Assertions.assertEquals("好友2", c.getRemark());

        // 再次添加同一联系人返回已有
        ContactDTO c2 = contactService.add(1L, 2L, "忽略");
        Assertions.assertEquals(c.getContactUserId(), c2.getContactUserId());

        // 更新备注
        ContactDTO updated = contactService.updateRemark(1L, 2L, "新备注");
        Assertions.assertEquals("新备注", updated.getRemark());

        // 允许发送消息 (未拉黑)
        chatService.sendMessage(1L, 2L, "hello");

        // 拉黑联系人 (owner=1 拉黑 2)
        ContactDTO blocked = contactService.setBlocked(1L, 2L, true);
        Assertions.assertTrue(blocked.getBlocked());

        // 发送消息应该抛 BLOCKED
        IllegalStateException ex = Assertions.assertThrows(IllegalStateException.class, () ->
                chatService.sendMessage(1L, 2L, "should fail")
        );
        Assertions.assertEquals("BLOCKED", ex.getMessage());

        // 解除拉黑后可再发
        ContactDTO unblocked = contactService.setBlocked(1L, 2L, false);
        Assertions.assertFalse(unblocked.getBlocked());
        chatService.sendMessage(1L, 2L, "after unblock");

        // 删除联系人
        contactService.remove(1L, 2L);
        List<ContactDTO> listAfter = contactService.list(1L);
        Assertions.assertEquals(baseSize, listAfter.size());
    }
}

