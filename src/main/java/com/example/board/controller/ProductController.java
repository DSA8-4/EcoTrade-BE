package com.example.board.controller;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.board.model.product.Product;
import com.example.board.model.product.ProductWriteForm;
import com.example.board.repository.ProductRepository;
import com.example.board.service.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
	private final ProductService productService;
	
	//상품 등록
	@PostMapping("/new")
	public ResponseEntity<Product> newProducts(@Valid @RequestBody ProductWriteForm productWriteForm) {
	    
		Product product = productWriteForm.toProduct(productWriteForm);
		
		Product createdProduct = productService.UploadProduct(product);
		return ResponseEntity.ok(createdProduct);
	    
	    
    }
	
	
	
}
