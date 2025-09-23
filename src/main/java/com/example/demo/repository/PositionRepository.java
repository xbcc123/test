package com.example.demo.repository;

import com.example.demo.model.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PositionRepository extends JpaRepository<Position, Long> {
    List<Position> findByDepartmentId(Long departmentId);
}

