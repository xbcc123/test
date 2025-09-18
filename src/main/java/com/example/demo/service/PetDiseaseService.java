package com.example.demo.service;

import com.example.demo.model.PetDisease;
import java.util.List;

public interface PetDiseaseService {
    PetDisease getById(Long id);
    List<PetDisease> getAll();
    boolean add(PetDisease petDisease);
    boolean update(PetDisease petDisease);
    boolean delete(Long id);
}

