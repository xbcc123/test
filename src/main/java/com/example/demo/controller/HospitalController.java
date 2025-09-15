package com.example.demo.controller;

import com.example.demo.model.Hospital;
import com.example.demo.repository.HospitalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    @PostMapping("/testdata")
    public List<Hospital> generateTestData() {
        List<Hospital> list = new java.util.ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            Hospital h = new Hospital();
            h.setName("测试医院名称" + i);
            h.setAddress("测试地址" + i);
            h.setPhone("1880000" + String.format("%04d", i));
            h.setRating(3.0 + (i % 3));
            h.setDescription("这是测试医院描述，编号：" + i + "，时间戳：" + System.currentTimeMillis());
            list.add(h);
        }
        return hospitalRepository.saveAll(list);
    }

    @GetMapping("/page")
    public Page<Hospital> getPage(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return hospitalRepository.findAll(pageable);
    }

    @GetMapping("/search")
    public Page<Hospital> search(@RequestParam String keyword,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return hospitalRepository.findByNameContainingOrAddressContaining(keyword, keyword, pageable);
    }
}
