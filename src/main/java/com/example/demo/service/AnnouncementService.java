package com.example.demo.service;

import com.example.demo.model.Announcement;
import com.example.demo.model.dto.AnnouncementDTO;
import com.example.demo.repository.AnnouncementRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AnnouncementService {
    @Autowired
    private AnnouncementRepository announcementRepository;

    private static final Logger log = LoggerFactory.getLogger(AnnouncementService.class);

    public Page<AnnouncementDTO> publicList(int page, int size, String type, String keyword) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AnnouncementDTO> result = announcementRepository.publicSearch(LocalDateTime.now(), blank(type), blank(keyword), pageable)
                .map(this::toDTO);
        log.debug("publicList page={}, size={}, type={}, keyword={}, resultCount={}", page, size, type, keyword, result.getNumberOfElements());
        return result;
    }

    public Page<AnnouncementDTO> adminList(int page, int size, String status, String type, String keyword) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AnnouncementDTO> result = announcementRepository.adminSearch(blank(status), blank(type), blank(keyword), pageable)
                .map(this::toDTO);
        log.debug("adminList page={}, size={}, status={}, type={}, keyword={}, resultCount={}", page, size, status, type, keyword, result.getNumberOfElements());
        return result;
    }

    public AnnouncementDTO getPublicDetail(Long id) {
        Optional<Announcement> opt = announcementRepository.findById(id);
        if (opt.isEmpty()) return null;
        Announcement a = opt.get();
        LocalDateTime now = LocalDateTime.now();
        if (!"PUBLISHED".equals(a.getStatus())) return null;
        if (a.getPublishTime()!=null && a.getPublishTime().isAfter(now)) return null;
        if (a.getExpireTime()!=null && !a.getExpireTime().isAfter(now)) return null;
        return toDTO(a);
    }

    public AnnouncementDTO getAdminDetail(Long id) {
        return announcementRepository.findById(id).map(this::toDTO).orElse(null);
    }

    public AnnouncementDTO create(AnnouncementDTO dto, Long creatorId) {
        Announcement a = new Announcement();
        apply(a, dto);
        a.setCreatorId(creatorId);
        if (a.getStatus() == null) a.setStatus("DRAFT");
        Announcement saved = announcementRepository.save(a);
        return toDTO(saved);
    }

    public AnnouncementDTO update(Long id, AnnouncementDTO dto) {
        Announcement a = announcementRepository.findById(id).orElse(null);
        if (a == null) return null;
        apply(a, dto);
        Announcement saved = announcementRepository.save(a);
        return toDTO(saved);
    }

    public boolean delete(Long id) {
        if (!announcementRepository.existsById(id)) return false;
        announcementRepository.deleteById(id);
        return true;
    }

    public AnnouncementDTO publish(Long id) {
        Announcement a = announcementRepository.findById(id).orElse(null);
        if (a == null) return null;
        a.setStatus("PUBLISHED");
        if (a.getPublishTime() == null) a.setPublishTime(LocalDateTime.now());
        return toDTO(announcementRepository.save(a));
    }

    public AnnouncementDTO unpublish(Long id) {
        Announcement a = announcementRepository.findById(id).orElse(null);
        if (a == null) return null;
        a.setStatus("DRAFT");
        return toDTO(announcementRepository.save(a));
    }

    private void apply(Announcement a, AnnouncementDTO dto) {
        if (dto.getTitle()!=null) a.setTitle(dto.getTitle());
        if (dto.getContent()!=null) a.setContent(dto.getContent());
        if (dto.getType()!=null) a.setType(dto.getType());
        if (dto.getStatus()!=null) a.setStatus(dto.getStatus());
        a.setPublishTime(dto.getPublishTime());
        a.setExpireTime(dto.getExpireTime());
    }

    private String blank(String s) { return (s==null||s.isBlank())?null:s; }

    private AnnouncementDTO toDTO(Announcement a) {
        AnnouncementDTO dto = new AnnouncementDTO();
        dto.setId(a.getId());
        dto.setTitle(a.getTitle());
        dto.setContent(a.getContent());
        dto.setType(a.getType());
        dto.setStatus(a.getStatus());
        dto.setPublishTime(a.getPublishTime());
        dto.setExpireTime(a.getExpireTime());
        dto.setCreatorId(a.getCreatorId());
        dto.setCreateTime(a.getCreateTime());
        dto.setUpdateTime(a.getUpdateTime());
        return dto;
    }
}
