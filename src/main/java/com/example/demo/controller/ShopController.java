package com.example.demo.controller;

import com.example.demo.model.Shop;
import com.example.demo.repository.ShopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shops")
public class ShopController {
    @Autowired
    private ShopRepository shopRepository;

    @GetMapping
    public List<Shop> getAll() {
        return shopRepository.findAll();
    }

    @GetMapping("/{id}")
    public Shop getById(@PathVariable Long id) {
        return shopRepository.findById(id).orElse(null);
    }

    @PostMapping
    public Shop create(@RequestBody Shop shop) {
        return shopRepository.save(shop);
    }

    @PutMapping("/{id}")
    public Shop update(@PathVariable Long id, @RequestBody Shop shop) {
        shop.setId(id);
        return shopRepository.save(shop);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        shopRepository.deleteById(id);
    }
}

