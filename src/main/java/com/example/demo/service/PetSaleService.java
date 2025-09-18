package com.example.demo.service;

import com.example.demo.model.PetSale;
import java.util.List;

public interface PetSaleService {
    PetSale getById(Long id);
    List<PetSale> getAll();
    List<PetSale> getOnSale();
    List<PetSale> getBySellerId(Long sellerId);
    boolean add(PetSale petSale);
    boolean update(PetSale petSale);
    boolean delete(Long id);
    boolean setStatus(Long id, String status);
}

