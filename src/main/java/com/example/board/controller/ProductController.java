package com.example.board.controller;

import com.example.board.dto.ProductDTO;
import com.example.board.model.member.Member;
import com.example.board.model.product.*;
import com.example.board.service.MemberService;
import com.example.board.service.ProductService;
import com.example.board.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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
	public ResponseEntity<Void> newProduct(@RequestHeader("Authorization") String authorizationHeader,
			@RequestBody ProductWriteForm productWriteForm) {
		try {
			// 토큰 추출 및 검증 (static 메서드 호출)
			String token = JwtTokenProvider.extractAndValidateToken(authorizationHeader, jwtTokenProvider);

			// 토큰에서 사용자 ID 추출
			String memberId = jwtTokenProvider.getUserIdFromToken(token);
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

			return ResponseEntity.ok().build();
		} catch (ResponseStatusException e) {
			log.error("Token validation error: {}", e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(null);
		} catch (Exception e) {
			log.error("Error occurred while registering product", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	@GetMapping("/list")
	public ResponseEntity<List<ProductDTO>> list(
	        @RequestParam(value = "searchText", required = false) String searchText,
	        @RequestParam(value = "page", defaultValue = "0") int page,  
	        @RequestParam(value = "size", defaultValue = "12") int size) { 

	    Pageable pageable = PageRequest.of(page, size);

	    List<Product> productList = (searchText != null && !searchText.isEmpty()) 
	            ? productService.findSearch(searchText, pageable).getContent()  // Page에서 내용만 가져옴
	            : productService.findAll(pageable).getContent();               // Page에서 내용만 가져옴

	    List<ProductDTO> productDTOs = productList.stream()
	            .map(ProductDTO::fromEntity)
	            .collect(Collectors.toList());

	    return ResponseEntity.ok(productDTOs);  
	}



	@DeleteMapping("/delete/{id}")
	public ResponseEntity<String> deleteProduct(@RequestHeader("Authorization") String authorizationHeader,
	                                            @PathVariable("id") Long id) {
	    try {
	        // JwtTokenProvider를 통해 토큰을 추출하고 유효성을 검사
	        String token = JwtTokenProvider.extractAndValidateToken(authorizationHeader, jwtTokenProvider);

	        // 토큰에서 사용자 ID를 추출
	        String userIdFromToken = jwtTokenProvider.getUserIdFromToken(token);

	        // 삭제하려는 제품의 정보를 가져옴
	        Product product = productService.findProduct(id);

	        // 제품의 소유자가 토큰의 사용자와 일치하는지 확인
	        if (!product.getMember().getMember_id().equals(userIdFromToken)) {
	            return ResponseEntity.status(HttpStatus.FORBIDDEN)
	                    .body("You are not authorized to delete this product.");
	        }

	        // 제품 삭제 수행
	        productService.deleteProduct(id);
	        return ResponseEntity.ok("Product deleted successfully.");
	    } catch (Exception e) {
	        log.error("Error deleting product: ", e);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting product.");
	    }
	}


	@GetMapping("/detail/{productId}")
	public ResponseEntity<ProductDTO> detail(@PathVariable("productId") Long productId) {
	    Optional<Product> productOpt = productService.findById(productId);

	    if (productOpt.isPresent()) {
	        Product product = productOpt.get();
	        product.addHit(); // Increase view count
	        productService.save(product); // Save changes
	        
	        ProductDTO productDTO = ProductDTO.fromEntity(product); // Populate DTO

	        return ResponseEntity.ok(productDTO);
	    } else {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
	    }
	}


	@PostMapping("/productLike/{productId}")
	public ResponseEntity<Boolean> likeProduct(@PathVariable("productId") Long productId,
			@RequestHeader("Authorization") String authorizationHeader) {
		try {
			// Authorization 헤더에서 JWT 토큰 추출
			String token = authorizationHeader.replace("Bearer ", "");

			// 토큰을 검증하고 사용자 정보를 추출
			if (!jwtTokenProvider.validateToken(token)) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
			}

			String memberId = jwtTokenProvider.getUserIdFromToken(token);

			// 사용자가 제품을 이미 좋아요 했는지 확인
			boolean isLiked = productService.isProductLiked(productId, memberId);

			if (isLiked) {
				// 이미 좋아요한 경우, 좋아요 취소
				productService.removeProductLike(productId, memberId);
				return ResponseEntity.ok(false); // 좋아요 취소
			} else {
				// 좋아요하지 않은 경우, 좋아요 추가
				productService.addProductLike(productId, memberId);
				return ResponseEntity.ok(true); // 좋아요 추가
			}
		} catch (Exception e) {
			log.error("제품 좋아요 처리 중 오류 발생", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
		}
	}

	@PutMapping("/update/{productId}")
	public ResponseEntity<Void> updateProduct(@RequestHeader("Authorization") String authorizationHeader,
	                                          @PathVariable("productId") Long productId,
	                                          @RequestBody ProductWriteForm productWriteForm) {
	    log.info("Updating product: {}", productWriteForm);
	    try {
	        // JwtTokenProvider를 통해 토큰을 추출하고 유효성을 검사
	        String token = JwtTokenProvider.extractAndValidateToken(authorizationHeader, jwtTokenProvider);
	        
	        // 토큰에서 사용자 ID를 추출
	        String userIdFromToken = jwtTokenProvider.getUserIdFromToken(token);
	        
	        // 기존 상품 불러오기
	        Optional<Product> existingProductOpt = productService.findById(productId);
	        if (!existingProductOpt.isPresent()) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
	        }

	        Product existingProduct = existingProductOpt.get();

	        // 상품 소유자가 토큰의 사용자와 일치하는지 확인
	        if (!existingProduct.getMember().getMember_id().equals(userIdFromToken)) {
	            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
	        }

	        // 상품 정보 업데이트
	        existingProduct.setTitle(productWriteForm.getTitle());
	        existingProduct.setContents(productWriteForm.getContents());
	        existingProduct.setPrice(productWriteForm.getPrice());
	        existingProduct.setCategory(productWriteForm.getCategory());

	        if (productWriteForm.getProductImages() != null && !productWriteForm.getProductImages().isEmpty()) {
	            // 1. 기존 이미지 삭제
	            existingProduct.getProductImages().clear();

	            // 2. 새로운 이미지를 추가
	            List<Image> images = productWriteForm.getProductImages().stream()
	                    .map(url -> new Image(url, existingProduct)).collect(Collectors.toList());

	            // 새로운 이미지 저장
	            productService.saveImages(images);

	            // 기존 제품의 이미지 리스트에 새로운 이미지 추가
	            existingProduct.getProductImages().addAll(images);
	        }

	        // 업데이트된 상품 정보 저장
	        productService.save(existingProduct);

	        return ResponseEntity.ok().build();
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
	        @RequestHeader("Authorization") String authorizationHeader,
	        @RequestBody Map<String, String> requestBody) {  // 요청 본문에서 JSON 객체를 받습니다
	    try {
	        log.info("Extracting token...");
	        // Extract the JWT token and validate it
	        String token = authorizationHeader.replace("Bearer ", "");
	        if (!jwtTokenProvider.validateToken(token)) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token.");
	        }

	        log.info("Extracting seller ID from token...");
	        // Get the sellerId from the token
	        String sellerId = jwtTokenProvider.getUserIdFromToken(token);

	        // Get buyer name from the request body
	        String name = requestBody.get("name");
	        if (name == null || name.isEmpty()) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Buyer name is required.");
	        }

	        log.info("Finding product...");
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

	        log.info("Finding buyer...");
	        // Get the buyer (member) by name
	        Member buyer = memberService.findByName(name);
	        if (buyer == null) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Buyer not found.");
	        }

	        // Check if the buyer is trying to buy their own product (buyer and seller should be different)
	        Member seller = product.getMember(); // Product 작성자 (판매자)
	        if (seller.getMember_id().equals(buyer.getMember_id())) {
	            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You cannot purchase your own product.");
	        }

	        log.info("Creating new purchase...");
	        // Create a new purchase
	        Purchase purchase = new Purchase();
	        purchase.setBuyer(buyer);
	        purchase.setProduct(product);
	        purchase.setPurchaseDate(LocalDateTime.now());

	        // Save the purchase
	        productService.savePurchase(purchase);

	        log.info("Updating product status to COMPLETED...");
	        // Update the product status to "Completed"
	        product.setStatus(ProductStatus.COMPLETED);
	        productService.save(product);

	        log.info("Updating member's eco points...");
	        // 적립 포인트 계산 및 저장
	        long ecoPointBuyer = Math.round(product.getPrice() * 0.01); // 구매자 포인트
	        long ecoPointSeller = Math.round(product.getPrice() * 0.005); // 판매자 포인트

	        // 구매자 포인트 적립
	        buyer.setEco_point(buyer.getEco_point() + ecoPointBuyer);
	        memberService.saveMember(buyer);

	        // 판매자 포인트 적립 (상품 작성자가 판매자)
	        seller.setEco_point(seller.getEco_point() + ecoPointSeller);
	        memberService.saveMember(seller);

	        return ResponseEntity.ok("Product purchased successfully.");
	    } catch (Exception e) {
	        log.error("Error during purchase", e);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during purchase.");
	    }
	}




}
