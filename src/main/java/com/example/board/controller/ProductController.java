package com.example.board.controller;

import com.example.board.model.product.Image;
import com.example.board.model.product.Product;
import com.example.board.model.product.ProductWriteForm;
import com.example.board.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
	private final ProductService productService;
	@Value("${file.upload.path}")
    private String uploadPath;
	

	// 상품 등록
	@PostMapping("/new")
	public ResponseEntity<Product> newProduct(@RequestBody ProductWriteForm productWriteForm) {
		log.info("product: {}", productWriteForm);
		try {
			

			Product product = ProductWriteForm.toProduct(productWriteForm);

			Product createdProduct = productService.uploadProduct(product);


			if (productWriteForm.getProductImages() != null && !productWriteForm.getProductImages().isEmpty()) {
				List<Image> images = productWriteForm.getProductImages().stream()
						.map(url -> new Image(url, createdProduct))
						.collect(Collectors.toList());

				// Save images
				productService.saveImages(images);
				createdProduct.setProductImages(images);
			}

			return ResponseEntity.ok(createdProduct);
		} catch (Exception e) {
			log.error("Error occurred while registering product", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	@GetMapping("/list")
	public ResponseEntity<List<Product>> list(@RequestParam(value = "searchText", required = false) String searchText) {
		List<Product> productList;

		if (searchText != null && !searchText.isEmpty()) {
			productList = productService.findSearch(searchText);
			log.info("Returning product list1: {}", productList);
		} else {
			productList = productService.findAll();
			log.info("Returning product list2: {}", productList);
		}
//	    log.info("Returning product list: {}", productList);
		return ResponseEntity.ok(productList);
		
	}
	
	@DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable("id") Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok("Product deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting product.");
        }
    }
	
	@GetMapping("/detail/{productId}")
	public ResponseEntity<Product> detail(@PathVariable("productId") Long productId) {
	    Optional<Product> productOpt = productService.findById(productId);

	    if (productOpt.isPresent()) {
	        Product product = productOpt.get();
	        product.addHit(); // 조회수 증가
	        productService.save(product); // 변경 사항 저장

	        return ResponseEntity.ok(product);
	    } else {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
	    }
	}
	
	// 상품 찜하기
	@PostMapping("/like/{productId}")
	public ResponseEntity<String> likeProduct(@PathVariable("productId") Long id) {
	    try {
	        productService.incrementHeart(id);
	        return ResponseEntity.ok("Product liked successfully.");
	    } catch (Exception e) {
	        log.error("Error occurred while liking product", e);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error liking product.");
	    }
	}

	
	


}
