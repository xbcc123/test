package com.example.demo.controller;

import com.example.demo.model.PetSale;
import com.example.demo.service.PetSaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.example.demo.model.CustomUserDetails;

@RestController
@RequestMapping("/api/pet-sale")
public class PetSaleController {
    @Autowired
    private PetSaleService petSaleService;

    @GetMapping("")
    public List<PetSale> getAll() {
        return petSaleService.getAll();
    }

    @GetMapping("/on-sale")
    public List<PetSale> getOnSale() {
        return petSaleService.getOnSale();
    }

    @GetMapping("/mine")
    public List<PetSale> getMine(@RequestParam Long sellerId) {
        return petSaleService.getBySellerId(sellerId);
    }

    @GetMapping("/{id}")
    public PetSale getById(@PathVariable Long id) {
        return petSaleService.getById(id);
    }

    @PostMapping("")
    public boolean add(@RequestBody PetSale petSale) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            petSale.setSellerId(userDetails.getUserId());
            petSale.setSellerName(userDetails.getUsername());
        }
        return petSaleService.add(petSale);
    }

    @PutMapping("/{id}")
    public boolean update(@PathVariable Long id, @RequestBody PetSale petSale) {
        petSale.setId(id);
        return petSaleService.update(petSale);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return petSaleService.delete(id);
    }

    @PutMapping("/{id}/status")
    public boolean setStatus(@PathVariable Long id, @RequestParam String status) {
        return petSaleService.setStatus(id, status);
    }
}
