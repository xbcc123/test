package com.example.demo.repository;

import com.example.demo.model.Hospital;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HospitalRepository extends JpaRepository<Hospital, Long> {
    Page<Hospital> findByNameContainingOrAddressContaining(String name, String address, Pageable pageable);
}
