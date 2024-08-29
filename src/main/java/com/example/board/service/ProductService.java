package com.example.board.service;

import com.example.board.model.product.Image;
import com.example.board.model.product.Product;
import com.example.board.repository.ImageRepository;
import com.example.board.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ProductService {
	
	private final ProductRepository productRepository;
	private final ImageRepository imageRepository;
	@Value("${file.upload.path}")
    private String uploadPath;
	

	// 상품 등록
	@Transactional
    public Product uploadProduct(Product product) {
        // 상품 저장
		return productRepository.save(product);
    }

	@Transactional
	public void saveImages(List<Image> images) {
		imageRepository.saveAll(images);
	}
	// 상품 상세 검색
	public Product findProduct(Long id) {
		Optional<Product> product = productRepository.findById(id);
		return product.orElse(null);
	}
	
	// 상품 찜하기
	@Transactional
	public void incrementHeart(Long productId) {
	    Product product = findProduct(productId);
	    if (product != null) {
	        product.addHeart();
	        productRepository.save(product);
	    }
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

	public int getTotal() {
		return (int) imageRepository.count();
	}

	public List<Product> findSearch(String searchText) {
	    return productRepository.findByTitleContaining(searchText);
	}
	
    
	public void deleteProduct(Long id) {
		productRepository.deleteById(id);
	}
	
	public Optional<Product> findById(Long productId) {
        return productRepository.findById(productId);
    }

    public void save(Product product) {
        productRepository.save(product);
    }
}
