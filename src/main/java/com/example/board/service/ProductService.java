package com.example.board.service;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.board.model.product.AttachedImage;
import com.example.board.model.product.Product;
import com.example.board.repository.ImageRepository;
import com.example.board.repository.ProductRepository;
import com.example.board.util.ImageService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ProductService {
	
	private final ProductRepository productRepository;
	private final ImageRepository imageRepository;
	private final ImageService imageService;

	// 상품 등록
	@Transactional
	public Product uploadProduct(Product product) {
	    // 상품 저장
	    Product savedProduct = productRepository.save(product);

	    // 파일 처리
//	    if (files != null) {
//	        for (MultipartFile file : files) {
//	            AttachedImage attachedImage = new AttachedImage();
//	            attachedImage.setOriginal_image(file.getOriginalFilename());
//	            attachedImage.setProduct(savedProduct);
//	            attachedImage.setImage_size(file.getSize());
//	            attachedImage.setSaved_image(file.getOriginalFilename());
//	            imageRepository.save(attachedImage); // 이미지 저장
//	        }
//	    }

	    return savedProduct;
	}
	
	// 상품 상세 검색
	public Product findProduct(Long id) {
		Optional<Product> product = productRepository.findById(id);
		return product.orElse(null);
	}
	
	// 상품 수정
	@Transactional
	public void updateProduct(Product updateProduct, boolean isFileRemoved, MultipartFile file) {
		Product findProduct = findProduct(updateProduct.getProduct_id());
		
		findProduct.setTitle(updateProduct.getTitle());
		findProduct.setContents(updateProduct.getContents());
		findProduct.setPrice(updateProduct.getPrice());
		
	}

	// 게시물 삭제
	@Transactional
	public void removeProduct(Product product) {
		productRepository.deleteById(product.getProduct_id());
	}

	// 게시글 전체 목록
	public List<Product> findAll() {
	    return productRepository.findAll();
	}
	

	public AttachedImage findFileByProductId(Product product) {
		return imageRepository.findByProduct(product);
	}

	public AttachedImage findFileByAttachedImageId(Long id) {
		return imageRepository.findById(id).orElse(null);
	}

	public int getTotal() {
		return (int) imageRepository.count();
	}

	public List<Product> findSearch(String searchText) {
	    return productRepository.findByTitleContaining(searchText);
	}
}
