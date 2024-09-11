package com.example.board.controller;

import com.example.board.dto.ProductDTO;
import com.example.board.model.product.Image;
import com.example.board.model.product.Product;
import com.example.board.model.product.ProductStatus;
import com.example.board.model.product.ProductWriteForm;
import com.example.board.model.product.Purchase;
import com.example.board.repository.ProductRepository;
import com.example.board.service.MemberService;
import com.example.board.service.ProductService;
import com.example.board.util.JwtTokenProvider;

import com.example.board.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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

	@PostMapping("/new")
	public ResponseEntity<Product> newProduct(@RequestHeader("Authorization") String authorizationHeader,
			@RequestBody ProductWriteForm productWriteForm) {
		try {
			String token = authorizationHeader.replace("Bearer ", "");
			if (!jwtTokenProvider.validateToken(token)) {
				log.info("invalid token");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
			}

			String memberId = jwtTokenProvider.getUserIdFromToken(token); // Extract user ID or other details from token
			log.info("User ID from token: {}", memberId);
			Product product = ProductWriteForm.toProduct(productWriteForm);
			product.setStatus(ProductStatus.TRADING); // 기본 상태 '거래중' 설정
			Product createdProduct = productService.uploadProduct(product, memberId);

			if (productWriteForm.getProductImages() != null && !productWriteForm.getProductImages().isEmpty()) {
				List<Image> images = productWriteForm.getProductImages().stream()
						.map(url -> new Image(url, createdProduct)).collect(Collectors.toList());
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
	public ResponseEntity<List<ProductDTO>> list(
			@RequestParam(value = "searchText", required = false) String searchText) {
		List<Product> productList = (searchText != null && !searchText.isEmpty())
				? productService.findSearch(searchText)
				: productService.findAll();

		List<ProductDTO> productDTOs = productList.stream().map(ProductDTO::fromEntity).collect(Collectors.toList());

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

	@PostMapping("/productLike/{productId}")
	public ResponseEntity<String> likeProduct(@PathVariable("productId") Long productId,
			@RequestHeader("Authorization") String authorizationHeader) {
		try {
			// Authorization 헤더에서 JWT 토큰 추출
			String token = authorizationHeader.replace("Bearer ", "");

			// 토큰을 검증하고 사용자 정보를 추출
			if (!jwtTokenProvider.validateToken(token)) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 토큰입니다.");
			}

			String memberId = jwtTokenProvider.getUserIdFromToken(token);

			// 사용자가 제품을 이미 좋아요 했는지 확인
			boolean isLiked = productService.isProductLiked(productId, memberId);

			if (isLiked) {
				// 이미 좋아요한 경우, 좋아요 취소
				productService.removeProductLike(productId, memberId);
				return ResponseEntity.ok("제품의 좋아요가 취소되었습니다.");
			} else {
				// 좋아요하지 않은 경우, 좋아요 추가
				productService.addProductLike(productId, memberId);
				return ResponseEntity.ok("제품이 좋아요되었습니다.");
			}
		} catch (Exception e) {
			log.error("제품 좋아요 처리 중 오류 발생", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("제품 좋아요 처리 중 오류가 발생했습니다.");
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

	@PutMapping("/updateStatus/{productId}")
	public ResponseEntity<String> updateProductStatus(@PathVariable("productId") Long productId,
			@RequestParam("status") ProductStatus status) {
		try {
			Optional<Product> productOpt = productService.findById(productId);
			if (!productOpt.isPresent()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found.");
			}

			Product product = productOpt.get();
			product.setStatus(status); // 상태 업데이트
			productService.save(product);

			return ResponseEntity.ok("Product status updated to " + status.getDescription());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating product status.");
		}
	}

	// 상품 구매
	@PostMapping("/purchase/{productId}")
	public ResponseEntity<String> purchaseProduct(@PathVariable("productId") Long productId,
			@RequestHeader("Authorization") String authorizationHeader) {
		try {
			// Extract the JWT token and validate it
			String token = authorizationHeader.replace("Bearer ", "");
			if (!jwtTokenProvider.validateToken(token)) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token.");
			}

			// Get the memberId from the token
			String memberId = jwtTokenProvider.getUserIdFromToken(token);

			// Find the product
			Optional<Product> productOpt = productService.findById(productId);
			if (!productOpt.isPresent()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found.");
			}

			Product product = productOpt.get();

			// Check if the product is already completed
			if (product.getStatus() == ProductStatus.COMPLETED) {
				return ResponseEntity.status(HttpStatus.CONFLICT).body("Product has already been sold.");
			}

			// Create a new purchase
			Purchase purchase = new Purchase();
			purchase.setBuyer(memberService.findMemberById(memberId)); // Get the buyer (member)
			purchase.setProduct(product);
			purchase.setPurchaseDate(LocalDateTime.now());

			// Save the purchase
			productService.savePurchase(purchase);

			// Update the product status to "Completed"
			product.setStatus(ProductStatus.COMPLETED);
			productService.save(product);

			return ResponseEntity.ok("Product purchased successfully.");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during purchase.");
		}
	}

}
