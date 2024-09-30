package com.example.board.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.board.model.ecoProduct.EcoProduct;
import com.example.board.model.product.Product;

public interface EcoProductRepository extends JpaRepository<EcoProduct, Long> {
	List<EcoProduct> findByTitleContaining(String searchText);
	
	Page<EcoProduct> findByTitleContaining(String searchText, Pageable pageable); 
	
    Page<EcoProduct> findAll(Pageable pageable);
}
