package com.example.demo.service;

import com.example.demo.model.PetDisease;
import com.example.demo.repository.PetDiseaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PetDiseaseServiceImpl implements PetDiseaseService {
    @Autowired
    private PetDiseaseRepository petDiseaseRepository;

    @Override
    public PetDisease getById(Long id) {
        return petDiseaseRepository.findById(id).orElse(null);
    }

    @Override
    public List<PetDisease> getAll() {
        return petDiseaseRepository.findAll();
    }

    @Override
    public boolean add(PetDisease petDisease) {
        return petDiseaseRepository.save(petDisease) != null;
    }

    @Override
    public boolean update(PetDisease petDisease) {
        if (petDisease.getId() == null || !petDiseaseRepository.existsById(petDisease.getId())) return false;
        return petDiseaseRepository.save(petDisease) != null;
    }

    @Override
    public boolean delete(Long id) {
        if (!petDiseaseRepository.existsById(id)) return false;
        petDiseaseRepository.deleteById(id);
        return true;
    }
}

