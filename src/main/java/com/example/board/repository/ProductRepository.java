package com.example.board.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.board.model.product.Product;

public interface ProductRepository extends JpaRepository<Product, Long>{
	//검색결과
	Page<Product> findByTitleContaining(String searchText, Pageable pageable);
}
