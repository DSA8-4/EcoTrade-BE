package com.example.board.repository;

import org.springframework.data.repository.CrudRepository;

import com.example.board.model.ecoProduct.EcoProduct;

public interface EcoProductRepository extends CrudRepository<EcoProduct, Long> {

}
