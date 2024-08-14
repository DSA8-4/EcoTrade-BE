package com.example.board.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.board.model.product.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
    // 검색 결과: title 필드를 기준으로 검색
	List<Product> findByTitleContaining(String searchText); // 제목 검색
}
