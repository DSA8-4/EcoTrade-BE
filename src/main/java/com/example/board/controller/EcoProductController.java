package com.example.board.controller;

import com.example.board.model.ecoProduct.EcoProduct;
import com.example.board.model.product.Product;
import com.example.board.model.product.ProductStatus;
import com.example.board.service.EcoProductService;
import com.example.board.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/EcoProduct")
@RequiredArgsConstructor
public class EcoProductController {
	private final EcoProductService ecoProductService;

	// 에코포인트로만 구매 가능한 상품 등록
	@PostMapping("/register")
	public ResponseEntity<String> registerEcoPointOnlyProduct(@RequestParam(name = "title") String title,
			@RequestParam(name = "contents") String contents, @RequestParam(name = "ecoPoints") Long ecoPoints) {
		try {
			log.info("Registering new eco-point only product...");
			// 상품 등록
			EcoProduct ecoProduct = new EcoProduct();
			ecoProduct.setTitle(title);
			ecoProduct.setContent(contents);
			ecoProduct.setPrice(ecoPoints); // 에코포인트로만 가격 설정
			ecoProduct.setStatus(ProductStatus.TRADING); // 거래 상태를 '거래중'으로 설정

			// 상품 저장
			ecoProductService.save(ecoProduct);
			
			return ResponseEntity.ok("Eco-point only product registered successfully.");
		} catch (Exception e) {
			log.error("Error during product registration", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during product registration.");
		}
	}
}
