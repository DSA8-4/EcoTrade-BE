package com.example.board.service;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.board.model.product.Product;
import com.example.board.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ProductService {
	private final ProductRepository productRepository;
	
	//상품 등록
	@Transactional
	public Product UploadProduct(Product product) {
		return productRepository.save(product);
	}
	
	//상품 상세 검색
	public Product findProduct(Long id) {
		Optional<Product> product = productRepository.findById(id);
		return product.orElse(null);
	}
	
	//상품 수정
//	@Transactional
//	public void updateProduct(Product updateProduct) {
//		Product findProduct = findProduct(updateProduct.getProduct_id());
//		
//		findProduct.setProduct_title(updateProduct.getProduct_title());
//		findProduct.setProduct_contents(updateProduct.getProduct_contents());
//		findProduct.setProduct_price(updateProduct.getProduct_price());
//		findProduct.setProduct_hit(updateProduct.getProduct_hit());
//		
//		productRepository.save(findProduct);
//	}
	
	//게시물 삭제
	@Transactional
	public void removeProduct(Product product) {
		productRepository.deleteById(product.getProduct_id());
	}
	
}





