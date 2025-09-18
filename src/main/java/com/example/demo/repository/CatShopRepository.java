package com.example.demo.repository;

import com.example.demo.model.CatShop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CatShopRepository extends JpaRepository<CatShop, Long> {
    List<CatShop> findByStatus(String status);
}

