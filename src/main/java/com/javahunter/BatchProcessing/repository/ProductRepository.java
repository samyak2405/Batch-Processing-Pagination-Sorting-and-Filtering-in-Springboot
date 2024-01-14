package com.javahunter.BatchProcessing.repository;

import com.javahunter.BatchProcessing.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product,Integer> {
    boolean existsByProductName(String productName);

    Product findByProductName(String productName);

    void deleteByProductName(String productName);

    Page<Product> findByProductNameContaining(String productName, Pageable pageable);
}
