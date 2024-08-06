package com.example.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.board.model.product.Product;

public interface ProductRepository extends JpaRepository<Product, Long>{

}
