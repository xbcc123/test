package com.example.demo.service;

import com.example.demo.model.Position;
import com.example.demo.model.Department;
import com.example.demo.repository.PositionRepository;
import com.example.demo.repository.DepartmentRepository;
import com.example.demo.model.dto.PositionSimpleDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PositionService {
    @Autowired
    private PositionRepository positionRepository;
    @Autowired
    private DepartmentRepository departmentRepository;

    public List<PositionSimpleDTO> listAll(Long departmentId) {
        List<Position> positions = departmentId == null ? positionRepository.findAll() : positionRepository.findByDepartmentId(departmentId);
        return positions.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public PositionSimpleDTO create(PositionSimpleDTO dto) {
        Position p = new Position();
        p.setName(dto.getName());
        p.setCode(dto.getCode());
        p.setDescription(dto.getDescription());
        if (dto.getDepartmentId() != null) {
            Department d = departmentRepository.findById(dto.getDepartmentId()).orElse(null);
            p.setDepartment(d);
        }
        return toDTO(positionRepository.save(p));
    }

    public PositionSimpleDTO update(Long id, PositionSimpleDTO dto) {
        Position p = positionRepository.findById(id).orElse(null);
        if (p == null) return null;
        p.setName(dto.getName());
        p.setCode(dto.getCode());
        p.setDescription(dto.getDescription());
        if (dto.getDepartmentId() != null) {
            Department d = departmentRepository.findById(dto.getDepartmentId()).orElse(null);
            p.setDepartment(d);
        } else {
            p.setDepartment(null);
        }
        return toDTO(positionRepository.save(p));
    }

    public boolean delete(Long id) {
        if (!positionRepository.existsById(id)) return false;
        positionRepository.deleteById(id);
        return true;
    }

    private PositionSimpleDTO toDTO(Position p) {
        if (p == null) return null;
        return new PositionSimpleDTO(p.getId(), p.getName(), p.getCode(), p.getDescription(), p.getDepartment()==null?null:p.getDepartment().getId());
    }
}

