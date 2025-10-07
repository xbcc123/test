package com.example.demo.repository;

import com.example.demo.model.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRepository extends JpaRepository<Video, Long> {
    Page<Video> findAllByOrderByUploadTimeDesc(Pageable pageable);
}

