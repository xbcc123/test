package com.example.demo.repository;

import com.example.demo.model.ContactRelation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ContactRelationRepository extends JpaRepository<ContactRelation, Long> {
    List<ContactRelation> findByOwnerUserIdOrderByRemarkAscCreateTimeAsc(Long ownerUserId);
    Optional<ContactRelation> findByOwnerUserIdAndContactUserId(Long ownerUserId, Long contactUserId);
    long countByOwnerUserIdAndContactUserId(Long ownerUserId, Long contactUserId);
}

