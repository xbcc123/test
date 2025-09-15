package com.example.demo.repository;

import com.example.demo.model.Encyclopedia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EncyclopediaRepository extends JpaRepository<Encyclopedia, Long> {
    Page<Encyclopedia> findByTitleContainingOrContentContaining(String title, String content, Pageable pageable);
}
