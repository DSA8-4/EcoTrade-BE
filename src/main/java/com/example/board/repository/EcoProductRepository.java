package com.example.board.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.board.model.ecoProduct.EcoProduct;

public interface EcoProductRepository extends JpaRepository<EcoProduct, Long> {
	List<EcoProduct> findByTitleContaining(String searchText);
}
