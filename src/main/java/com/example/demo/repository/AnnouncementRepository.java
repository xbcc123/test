package com.example.demo.repository;

import com.example.demo.model.Announcement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    @Query("SELECT a FROM Announcement a WHERE a.status='PUBLISHED' " +
            "AND (a.publishTime IS NULL OR a.publishTime <= :now) " +
            "AND (a.expireTime IS NULL OR a.expireTime > :now) " +
            "AND (:type IS NULL OR a.type = :type) " +
            "AND (:kw IS NULL OR a.title LIKE %:kw% OR a.content LIKE %:kw%) " +
            "ORDER BY CASE WHEN a.publishTime IS NULL THEN 1 ELSE 0 END, a.publishTime DESC, a.createTime DESC")
    Page<Announcement> publicSearch(@Param("now") LocalDateTime now,
                                    @Param("type") String type,
                                    @Param("kw") String keyword,
                                    Pageable pageable);

    @Query("SELECT a FROM Announcement a WHERE (:status IS NULL OR a.status = :status) " +
            "AND (:type IS NULL OR a.type = :type) " +
            "AND (:kw IS NULL OR a.title LIKE %:kw% OR a.content LIKE %:kw%) " +
            "ORDER BY a.updateTime DESC")
    Page<Announcement> adminSearch(@Param("status") String status,
                                   @Param("type") String type,
                                   @Param("kw") String keyword,
                                   Pageable pageable);
}
