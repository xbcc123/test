package com.example.demo.controller;

import com.example.demo.model.PetDisease;
import com.example.demo.service.PetDiseaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/pet-disease")
public class PetDiseaseController {
    @Autowired
    private PetDiseaseService petDiseaseService;

    @GetMapping("/{id}")
    public PetDisease getById(@PathVariable Long id) {
        return petDiseaseService.getById(id);
    }

    @GetMapping
    public List<PetDisease> getAll() {
        return petDiseaseService.getAll();
    }

    @PostMapping
    public boolean add(@RequestBody PetDisease petDisease) {
        return petDiseaseService.add(petDisease);
    }

    @PutMapping
    public boolean update(@RequestBody PetDisease petDisease) {
        return petDiseaseService.update(petDisease);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return petDiseaseService.delete(id);
    }

    @PutMapping("/{id}")
    public boolean updateById(@PathVariable Long id, @RequestBody PetDisease petDisease) {
        petDisease.setId(id);
        return petDiseaseService.update(petDisease);
    }
}
