package com.example.demo.controller;

import com.example.demo.model.Encyclopedia;
import com.example.demo.repository.EncyclopediaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/encyclopedias")
public class EncyclopediaController {
    @Autowired
    private EncyclopediaRepository encyclopediaRepository;

    @GetMapping
    public List<Encyclopedia> getAll() {
        return encyclopediaRepository.findAll();
    }

    @GetMapping("/{id}")
    public Encyclopedia getById(@PathVariable Long id) {
        return encyclopediaRepository.findById(id).orElse(null);
    }

    @PostMapping
    public Encyclopedia create(@RequestBody Encyclopedia encyclopedia) {
        return encyclopediaRepository.save(encyclopedia);
    }

    @PutMapping("/{id}")
    public Encyclopedia update(@PathVariable Long id, @RequestBody Encyclopedia encyclopedia) {
        encyclopedia.setId(id);
        return encyclopediaRepository.save(encyclopedia);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        encyclopediaRepository.deleteById(id);
    }
}

