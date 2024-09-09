package com.example.board.controller;

import com.example.board.dto.ProductDTO;
import com.example.board.model.member.Member;
import com.example.board.model.product.Image;
import com.example.board.model.product.Product;
import com.example.board.model.product.ProductWriteForm;
import com.example.board.service.MemberService;
import com.example.board.service.ProductService;
import com.example.board.util.JwtTokenProvider;
import com.example.board.util.JwtTokenUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
	private final ProductService productService;
	private final MemberService memberService;
	private final JwtTokenProvider jwtTokenProvider;


	// 상품 등록
	@PostMapping("/new")
	public ResponseEntity<Product> newProduct(@RequestBody ProductWriteForm productWriteForm) {
		try {
			log.info("product: {}", productWriteForm);

			// ProductWriteForm을 Product로 변환
			Product product = ProductWriteForm.toProduct(productWriteForm);
			String memberId = productWriteForm.getMember_id(); // memberId를 가져옴

			// 상품 등록
			Product createdProduct = productService.uploadProduct(product, memberId);

			// 이미지 처리
			if (productWriteForm.getProductImages() != null && !productWriteForm.getProductImages().isEmpty()) {
				List<Image> images = productWriteForm.getProductImages().stream()
						.map(url -> new Image(url, createdProduct)).collect(Collectors.toList());

				// 이미지 저장
				productService.saveImages(images);
				createdProduct.setProductImages(images);
			}

			// 등록된 상품 반환
			return ResponseEntity.ok(createdProduct);
		} catch (Exception e) {
			log.error("Error occurred while registering product", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	@GetMapping("/list")
	public ResponseEntity<List<ProductDTO>> list(@RequestParam(value = "searchText", required = false) String searchText) {
	    List<Product> productList = (searchText != null && !searchText.isEmpty()) 
	        ? productService.findSearch(searchText)
	        : productService.findAll();

	    List<ProductDTO> productDTOs = productList.stream()
	        .map(ProductDTO::fromEntity)
	        .collect(Collectors.toList());

	    return ResponseEntity.ok(productDTOs);
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

	@PutMapping("/update/{productId}")
	public ResponseEntity<Product> updateProduct(@PathVariable("productId") Long productId,
			@RequestBody ProductWriteForm productWriteForm) {
		log.info("Updating product: {}", productWriteForm);
		try {
			// 기존 상품 불러오기
			Optional<Product> existingProductOpt = productService.findById(productId);
			if (!existingProductOpt.isPresent()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
			}

			Product existingProduct = existingProductOpt.get();

			// 상품 정보 업데이트
			existingProduct.setTitle(productWriteForm.getTitle());
			existingProduct.setContents(productWriteForm.getContents());
			existingProduct.setPrice(productWriteForm.getPrice());
			existingProduct.setCategory(productWriteForm.getCategory());

			// 기존 이미지 유지, 새로운 이미지 추가
			if (productWriteForm.getProductImages() != null && !productWriteForm.getProductImages().isEmpty()) {
				List<Image> images = productWriteForm.getProductImages().stream()
						.map(url -> new Image(url, existingProduct)).collect(Collectors.toList());

				// 기존 이미지에 새로운 이미지 추가
				productService.saveImages(images);
				existingProduct.getProductImages().addAll(images);
			}

			// 업데이트된 상품 정보 저장 (반환값 없이 저장)
			productService.save(existingProduct);

			return ResponseEntity.ok(existingProduct);
		} catch (Exception e) {
			log.error("Error occurred while updating product", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

}
