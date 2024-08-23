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
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
//@CrossOrigin(origins = "http://127.0.0.1:5500")
public class ProductController {
	private final ProductService productService;
	@Value("${file.upload.path}")
    private String uploadPath;
	

	// 상품 등록
	@PostMapping("/new")
	public ResponseEntity<Product> newProduct(@RequestBody ProductWriteForm productWriteForm) {
		try {
			log.info("product: {}", productWriteForm);
			// Convert ProductWriteForm to Product entity
			Product product = ProductWriteForm.toProduct(productWriteForm);

			// Save product using service
			Product createdProduct = productService.uploadProduct(product);

			// Handle images
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
	
	@GetMapping("/display")
	public ResponseEntity<Resource> display(@RequestParam("filename") String filename) {
	    Path filePath = Paths.get(uploadPath).resolve(filename);
	    Resource resource = new FileSystemResource(filePath);

	    if (!resource.exists()) {
	        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	    }

	    HttpHeaders headers = new HttpHeaders();
	    try {
	        headers.add(HttpHeaders.CONTENT_TYPE, Files.probeContentType(filePath));
	    } catch (IOException e) {
	        e.printStackTrace();
	    }

	    return new ResponseEntity<>(resource, headers, HttpStatus.OK);
	}
}
