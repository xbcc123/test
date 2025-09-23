package com.example.demo.config;

import com.example.demo.model.Announcement;
import com.example.demo.repository.AnnouncementRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    private final AnnouncementRepository announcementRepository;

    public DataInitializer(AnnouncementRepository announcementRepository) {
        this.announcementRepository = announcementRepository;
    }

    @Override
    public void run(String... args) {
        long count = announcementRepository.count();
        if (count == 0) {
            log.info("Seeding sample announcements (table empty)...");
            Announcement a1 = new Announcement();
            a1.setTitle("系统维护通知");
            a1.setContent("本周六凌晨 02:00-04:00 将进行系统维护，期间服务可能短暂不可用。感谢理解。");
            a1.setType("SYSTEM");
            a1.setStatus("PUBLISHED");
            a1.setPublishTime(LocalDateTime.now().minusHours(1));
            a1.setCreatorId(1L);

            Announcement a2 = new Announcement();
            a2.setTitle("新品功能预告");
            a2.setContent("我们将在下周发布全新功能，敬请期待！(该公告设置了未来发布时间，因此现在不会出现在公开列表)");
            a2.setType("GENERAL");
            a2.setStatus("PUBLISHED");
            a2.setPublishTime(LocalDateTime.now().plusDays(1)); // future; will be filtered out in public list
            a2.setCreatorId(1L);

            announcementRepository.save(a1);
            announcementRepository.save(a2);
            log.info("Sample announcements inserted.");
        } else {
            log.info("Announcement table already has {} records; no seed performed.", count);
        }
    }
}

