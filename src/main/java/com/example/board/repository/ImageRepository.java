package com.example.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.board.model.product.AttachedImage;
import com.example.board.model.product.Product;

public interface ImageRepository extends JpaRepository<AttachedImage, Long> {
	AttachedImage findByProduct(Product product);
}
