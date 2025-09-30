package com.example.demo.service;

import com.example.demo.model.ContactRelation;
import com.example.demo.model.dto.BasicUserDTO;
import com.example.demo.model.dto.ContactDTO;
import com.example.demo.repository.ContactRelationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ContactService {
    @Autowired
    private ContactRelationRepository contactRelationRepository;
    @Autowired
    private UserCacheService userCacheService;

    public List<ContactDTO> list(Long ownerUserId) {
        return contactRelationRepository.findByOwnerUserIdOrderByRemarkAscCreateTimeAsc(ownerUserId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional
    public ContactDTO add(Long ownerUserId, Long contactUserId, String remark) {
        if (ownerUserId.equals(contactUserId)) throw new IllegalArgumentException("CANNOT_ADD_SELF");
        Optional<ContactRelation> exist = contactRelationRepository.findByOwnerUserIdAndContactUserId(ownerUserId, contactUserId);
        if (exist.isPresent()) return toDTO(exist.get());
        ContactRelation r = new ContactRelation();
        r.setOwnerUserId(ownerUserId);
        r.setContactUserId(contactUserId);
        r.setRemark(remark);
        return toDTO(contactRelationRepository.save(r));
    }

    @Transactional
    public ContactDTO updateRemark(Long ownerUserId, Long contactUserId, String remark) {
        ContactRelation r = contactRelationRepository.findByOwnerUserIdAndContactUserId(ownerUserId, contactUserId)
                .orElseThrow(() -> new IllegalArgumentException("NOT_FOUND"));
        r.setRemark(remark);
        return toDTO(contactRelationRepository.save(r));
    }

    @Transactional
    public ContactDTO setBlocked(Long ownerUserId, Long contactUserId, boolean blocked) {
        ContactRelation r = contactRelationRepository.findByOwnerUserIdAndContactUserId(ownerUserId, contactUserId)
                .orElseThrow(() -> new IllegalArgumentException("NOT_FOUND"));
        r.setBlocked(blocked);
        return toDTO(contactRelationRepository.save(r));
    }

    @Transactional
    public void remove(Long ownerUserId, Long contactUserId) {
        contactRelationRepository.findByOwnerUserIdAndContactUserId(ownerUserId, contactUserId)
                .ifPresent(rel -> contactRelationRepository.deleteById(rel.getId()));
    }

    public boolean isBlocked(Long ownerUserId, Long contactUserId) {
        return contactRelationRepository.findByOwnerUserIdAndContactUserId(ownerUserId, contactUserId)
                .map(ContactRelation::getBlocked).orElse(false);
    }

    private ContactDTO toDTO(ContactRelation r) {
        BasicUserDTO user = userCacheService.getBasicUser(r.getContactUserId());
        return new ContactDTO(r.getContactUserId(), r.getRemark(), r.getBlocked(), r.getCreateTime(), user);
    }
}

