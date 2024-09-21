package com.example.board.service;


import org.springframework.stereotype.Service;

import com.example.board.model.ecoProduct.EcoProduct;
import com.example.board.repository.EcoProductRepository;
import com.example.board.repository.ImageRepository;
import com.example.board.repository.MemberRepository;
import com.example.board.repository.ProductLikeRepository;
import com.example.board.repository.ProductRepository;
import com.example.board.repository.PurchaseRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class EcoProductService {
	private final EcoProductRepository ecoProductRepository;
	
	public void save(EcoProduct ecoProduct) {
		ecoProductRepository.save(ecoProduct);
	}
   
}
