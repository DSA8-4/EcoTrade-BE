package com.example.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.board.model.ecoProduct.EcoProductImage;

public interface EcoProductImageRepository extends JpaRepository<EcoProductImage, Long> {

}
