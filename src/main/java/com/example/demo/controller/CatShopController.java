package com.example.demo.controller;

import com.example.demo.model.CatShop;
import com.example.demo.service.CatShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catshop")
public class CatShopController {
    @Autowired
    private CatShopService catShopService;

    @GetMapping("/on-sale")
    public List<CatShop> getOnSale() {
        return catShopService.getOnSale();
    }

    @GetMapping("")
    public List<CatShop> getAll() {
        return catShopService.getAll();
    }

    @GetMapping("/{id}")
    public CatShop getById(@PathVariable Long id) {
        return catShopService.getById(id);
    }

    @PostMapping("")
    public int add(@RequestBody CatShop catShop) {
        return catShopService.add(catShop);
    }

    @PutMapping("/{id}")
    public int update(@PathVariable Long id, @RequestBody CatShop catShop) {
        catShop.setId(id);
        return catShopService.update(catShop);
    }

    @DeleteMapping("/{id}")
    public int delete(@PathVariable Long id) {
        return catShopService.delete(id);
    }

    @PutMapping("/{id}/status")
    public int setStatus(@PathVariable Long id, @RequestParam String status) {
        return catShopService.setStatus(id, status);
    }

    @PostMapping("/buy/{id}")
    public int buy(@PathVariable Long id) {
        // 购买即将状态设为“已售”
        return catShopService.setStatus(id, "已售");
    }
}
