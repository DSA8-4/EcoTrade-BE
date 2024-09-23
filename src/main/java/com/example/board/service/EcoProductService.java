package com.example.board.service;


import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.board.model.ecoProduct.EcoProduct;
import com.example.board.model.ecoProduct.EcoProductImage;
import com.example.board.model.product.Image;
import com.example.board.model.product.Product;
import com.example.board.model.product.Purchase;
import com.example.board.repository.EcoProductImageRepository;
import com.example.board.repository.EcoProductRepository;
import com.example.board.repository.ImageRepository;
import com.example.board.repository.MemberRepository;
import com.example.board.repository.ProductLikeRepository;
import com.example.board.repository.ProductRepository;
import com.example.board.repository.PurchaseRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class EcoProductService {
	private final EcoProductRepository ecoProductRepository;
	private final EcoProductImageRepository ecoProductImageRepository;
	private final PurchaseRepository purchaseRepository;
	
	public void save(EcoProduct ecoProduct) {
		ecoProductRepository.save(ecoProduct);
	}
    
	public Optional<EcoProduct> findById(Long id) {
        return ecoProductRepository.findById(id);
    }
	
	@Transactional
	public void saveImages(List<EcoProductImage> images) {
		ecoProductImageRepository.saveAll(images);
	}
	
	public List<EcoProduct> findSearch(String searchText) {
		return ecoProductRepository.findByTitleContaining(searchText);
	}
	
	public List<EcoProduct> findAll() {
		return ecoProductRepository.findAll();
	}
	
	public void deleteProduct(Long id) {
		ecoProductRepository.deleteById(id);
	}
	
	@Transactional
    public void savePurchase(Purchase purchase) {
        purchaseRepository.save(purchase);
    }
}
