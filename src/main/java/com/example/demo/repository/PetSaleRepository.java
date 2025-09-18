package com.example.demo.repository;

import com.example.demo.model.PetSale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PetSaleRepository extends JpaRepository<PetSale, Long> {
    List<PetSale> findBySellerId(Long sellerId);
    List<PetSale> findByStatus(String status);
}

