package com.example.demo.controller;

import com.example.demo.model.Hospital;
import com.example.demo.repository.HospitalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hospitals")
public class HospitalController {
    @Autowired
    private HospitalRepository hospitalRepository;

    @GetMapping
    public List<Hospital> getAll() {
        return hospitalRepository.findAll();
    }

    @GetMapping("/{id}")
    public Hospital getById(@PathVariable Long id) {
        return hospitalRepository.findById(id).orElse(null);
    }

    @PostMapping
    public Hospital create(@RequestBody Hospital hospital) {
        return hospitalRepository.save(hospital);
    }

    @PutMapping("/{id}")
    public Hospital update(@PathVariable Long id, @RequestBody Hospital hospital) {
        hospital.setId(id);
        return hospitalRepository.save(hospital);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        hospitalRepository.deleteById(id);
    }
}

