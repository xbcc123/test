package com.example.demo.service;

import com.example.demo.model.PetSale;
import com.example.demo.repository.PetSaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PetSaleServiceImpl implements PetSaleService {
    @Autowired
    private PetSaleRepository petSaleRepository;

    @Override
    public PetSale getById(Long id) {
        return petSaleRepository.findById(id).orElse(null);
    }

    @Override
    public List<PetSale> getAll() {
        return petSaleRepository.findAll();
    }

    @Override
    public List<PetSale> getOnSale() {
        return petSaleRepository.findByStatus("在售");
    }

    @Override
    public List<PetSale> getBySellerId(Long sellerId) {
        return petSaleRepository.findBySellerId(sellerId);
    }

    @Override
    public boolean add(PetSale petSale) {
        if (petSale.getStatus() == null || petSale.getStatus().isEmpty()) {
            petSale.setStatus("在售");
        }
        return petSaleRepository.save(petSale) != null;
    }

    @Override
    public boolean update(PetSale petSale) {
        if (petSale.getId() == null || !petSaleRepository.existsById(petSale.getId())) return false;
        return petSaleRepository.save(petSale) != null;
    }

    @Override
    public boolean delete(Long id) {
        if (!petSaleRepository.existsById(id)) return false;
        petSaleRepository.deleteById(id);
        return true;
    }

    @Override
    public boolean setStatus(Long id, String status) {
        PetSale petSale = getById(id);
        if (petSale == null) return false;
        petSale.setStatus(status);
        return petSaleRepository.save(petSale) != null;
    }
}
