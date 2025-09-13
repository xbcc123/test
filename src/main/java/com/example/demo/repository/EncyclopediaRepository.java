package com.example.demo.repository;

import com.example.demo.model.Encyclopedia;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EncyclopediaRepository extends JpaRepository<Encyclopedia, Long> {
}

