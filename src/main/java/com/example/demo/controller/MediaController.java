package com.example.demo.controller;

import com.example.demo.model.Media;
import com.example.demo.repository.MediaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/media")
public class MediaController {
    @Autowired
    private MediaRepository mediaRepository;

    @GetMapping
    public List<Media> getAll() {
        return mediaRepository.findAll();
    }

    @GetMapping("/{id}")
    public Media getById(@PathVariable Long id) {
        return mediaRepository.findById(id).orElse(null);
    }

    @PostMapping
    public Media create(@RequestBody Media media) {
        return mediaRepository.save(media);
    }

    @PutMapping("/{id}")
    public Media update(@PathVariable Long id, @RequestBody Media media) {
        media.setId(id);
        return mediaRepository.save(media);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        mediaRepository.deleteById(id);
    }
}

