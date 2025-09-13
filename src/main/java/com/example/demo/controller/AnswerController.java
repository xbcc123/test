package com.example.demo.controller;

import com.example.demo.model.Answer;
import com.example.demo.repository.AnswerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/answers")
public class AnswerController {
    @Autowired
    private AnswerRepository answerRepository;

    @GetMapping
    public List<Answer> getAll() {
        return answerRepository.findAll();
    }

    @GetMapping("/{id}")
    public Answer getById(@PathVariable Long id) {
        return answerRepository.findById(id).orElse(null);
    }

    @PostMapping
    public Answer create(@RequestBody Answer answer) {
        return answerRepository.save(answer);
    }

    @PutMapping("/{id}")
    public Answer update(@PathVariable Long id, @RequestBody Answer answer) {
        answer.setId(id);
        return answerRepository.save(answer);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        answerRepository.deleteById(id);
    }
}

