package com.example.board.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.board.model.product.Product;
import com.example.board.service.ProductService;
import com.example.board.util.ImageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class ProductController {
	private final ImageService imageService;
	private final ProductService productService;
	@Value("${file.upload.path}")
    private String uploadPath;
	

	// 상품 등록
	@PostMapping("/new")
	public ResponseEntity<Product> newProduct(@RequestParam("title") String title,
			@RequestParam("contents") String contents, @RequestParam("price") Long price,
			@RequestParam(value = "created_time", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime created_time,
			@RequestParam(value = "files", required = false) MultipartFile[] files) {

		if (created_time == null) {
			created_time = LocalDateTime.now();
		}

		Product product = new Product();
		product.setTitle(title);
		product.setContents(contents);
		product.setPrice(price);
		product.setCreated_time(created_time);

		try {
			// 상품 등록 처리 (파일 업로드 포함)
			Product createdProduct = productService.uploadProduct(product, files);

			return ResponseEntity.ok(createdProduct);
		} catch (Exception e) {
			log.error("상품 등록 중 오류 발생", e);
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
