package com.example.demo.service;

import com.example.demo.model.CatShop;
import com.example.demo.repository.CatShopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CatShopService {
    @Autowired
    private CatShopRepository catShopRepository;

    public List<CatShop> getAll() {
        return catShopRepository.findAll();
    }

    public List<CatShop> getOnSale() {
        return catShopRepository.findByStatus("在售");
    }

    public CatShop getById(Long id) {
        return catShopRepository.findById(id).orElse(null);
    }

    public int add(CatShop catShop) {
        return catShopRepository.save(catShop) != null ? 1 : 0;
    }

    public int update(CatShop catShop) {
        if (catShop.getId() == null || !catShopRepository.existsById(catShop.getId())) return 0;
        return catShopRepository.save(catShop) != null ? 1 : 0;
    }

    public int delete(Long id) {
        if (!catShopRepository.existsById(id)) return 0;
        catShopRepository.deleteById(id);
        return 1;
    }

    public int setStatus(Long id, String status) {
        Optional<CatShop> optional = catShopRepository.findById(id);
        if (optional.isEmpty()) return 0;
        CatShop cat = optional.get();
        cat.setStatus(status);
        return catShopRepository.save(cat) != null ? 1 : 0;
    }
}
