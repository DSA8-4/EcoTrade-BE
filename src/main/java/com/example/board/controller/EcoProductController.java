package com.example.board.controller;

import com.example.board.dto.EcoProductDTO;
import com.example.board.dto.EcoProductPurchaseDTO;
import com.example.board.dto.EcoProductPurchaseHistoryDTO;
import com.example.board.dto.ProductDTO;
import com.example.board.dto.PurchaseDTO;
import com.example.board.model.ecoProduct.EcoProduct;
import com.example.board.model.ecoProduct.EcoProductImage;
import com.example.board.model.ecoProduct.EcoProductPurchase;
import com.example.board.model.ecoProduct.EcoProductStatus;
import com.example.board.model.ecoProduct.EcoProductWriteForm;
import com.example.board.model.member.Member;
import com.example.board.model.product.Image;
import com.example.board.model.product.Product;
import com.example.board.model.product.ProductStatus;
import com.example.board.model.product.Purchase;
import com.example.board.service.EcoProductService;
import com.example.board.service.MemberService;
import com.example.board.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/EcoProduct")
@RequiredArgsConstructor
public class EcoProductController {
	private final EcoProductService ecoProductService;
	private final MemberService memberService;
	private final JwtTokenProvider jwtTokenProvider;

	// 에코포인트로만 구매 가능한 상품 등록
	@PostMapping("/register")
	public ResponseEntity<String> registerEcoPointOnlyProduct(@RequestBody EcoProductWriteForm ecoProductWriteForm) {
		try {
			EcoProduct ecoProduct = EcoProductWriteForm.toEcoProduct(ecoProductWriteForm);
			List<String> ecoProductWriteFormImages = ecoProductWriteForm.getEcoProductImages();
			if (ecoProductWriteFormImages != null && !ecoProductWriteFormImages.isEmpty()) {
				List<EcoProductImage> ecoProductImages = ecoProductWriteFormImages.stream()
						.map(url -> new EcoProductImage(url, ecoProduct)).collect(Collectors.toList());
				ecoProduct.setEcoProductImages(ecoProductImages);
			}

			ecoProductService.save(ecoProduct);

			return ResponseEntity.ok("Eco-point only product registered successfully.");
		} catch (Exception e) {
			log.error("Error during product registration", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during product registration.");
		}
	}

	@PutMapping("/update/{id}")
	public ResponseEntity<String> updateEcoPointOnlyProduct(@PathVariable("id") Long id,
			@RequestBody EcoProductWriteForm ecoProductWriteForm) {

		log.info("Updating eco-point only product with ID: {}", id);

		try {
			// 상품 조회
			Optional<EcoProduct> optionalEcoProduct = ecoProductService.findById(id);
			if (optionalEcoProduct.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found with ID: " + id);
			}

			EcoProduct ecoProduct = optionalEcoProduct.get();

			ecoProduct.setTitle(ecoProductWriteForm.getTitle());
			ecoProduct.setContent(ecoProductWriteForm.getContent());
			ecoProduct.setPrice(ecoProductWriteForm.getEcoPoints());

			// Handle image updates
			if (ecoProductWriteForm.getEcoProductImages() != null
					&& ecoProductWriteForm.getEcoProductImages().isEmpty()) {
				// 1. 기존 이미지 삭제
				ecoProduct.getEcoProductImages().clear();

				// 2. 새로운 이미지를 추가
				List<EcoProductImage> images = ecoProductWriteForm.getEcoProductImages().stream()
						.map(url -> new EcoProductImage(url, ecoProduct)).collect(Collectors.toList());

				// 새로운 이미지 저장
				ecoProductService.saveImages(images);

				// 기존 제품의 이미지 리스트에 새로운 이미지 추가
				ecoProduct.getEcoProductImages().addAll(images);
			}

			// Save updated product
			ecoProductService.save(ecoProduct);

			return ResponseEntity.ok("Eco-point only product updated successfully.");
		} catch (Exception e) {
			log.error("Error during product update", e); // 예외 메시지 기록
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error during product update: " + e.getMessage());
		}
	}

	@GetMapping("/detail/{id}")
	public ResponseEntity<EcoProductDTO> detail(@PathVariable("id") Long id) {
		Optional<EcoProduct> ecoProductOpt = ecoProductService.findById(id);

		if (ecoProductOpt.isPresent()) {
			EcoProduct ecoProduct = ecoProductOpt.get();
			ecoProductService.save(ecoProduct); // 변경 사항 저장

			EcoProductDTO ecoProductDTO = EcoProductDTO.fromEntity(ecoProduct);

			return ResponseEntity.ok(ecoProductDTO);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<String> deleteProduct(@PathVariable("id") Long id) {
		ecoProductService.deleteProduct(id);
		return ResponseEntity.ok("Product deleted successfully.");
	}

	@GetMapping("/list")
	public ResponseEntity<List<EcoProductDTO>> list(
			@RequestParam(value = "searchText", required = false) String searchText) {
		List<EcoProduct> ecoProductList = (searchText != null && !searchText.isEmpty())
				? ecoProductService.findSearch(searchText)
				: ecoProductService.findAll();
		List<EcoProductDTO> ecoProductDTOs = ecoProductList.stream().map(EcoProductDTO::fromEntity)
				.collect(Collectors.toList());
		return ResponseEntity.ok(ecoProductDTOs);
	}

	@PostMapping("/purchase/{id}")
	public ResponseEntity<EcoProductPurchaseDTO> purchaseEcoProduct(@PathVariable("id") Long id,
			@RequestHeader("Authorization") String authorizationHeader, @RequestBody String deliveryAddress) { // 주소를
																												// RequestBody로
																												// 받음
		try {
			log.info("Extracting token...");
			// Extract the JWT token and validate it
			String token = authorizationHeader.replace("Bearer ", "");
			if (!jwtTokenProvider.validateToken(token)) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
			}

			log.info("Extracting member ID from token...");
			// Get the memberId from the token
			String memberId = jwtTokenProvider.getUserIdFromToken(token);

			log.info("Finding product...");
			// Find the EcoProduct
			Optional<EcoProduct> ecoProductOpt = ecoProductService.findById(id);
			if (!ecoProductOpt.isPresent()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
			}

			EcoProduct ecoProduct = ecoProductOpt.get();

			// 구매자 정보 조회
			Member buyer = memberService.findMemberById(memberId); // Get the buyer (member)

			// 포인트 확인
			if (buyer.getEco_point() < ecoProduct.getPrice()) {
				// 포인트 부족으로 인한 예외 처리
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // 에러 메시지는 별도의 처리로 변경 가능
			}

			log.info("Creating new purchase...");
			// Create a new purchase
			EcoProductPurchase ecoPurchase = new EcoProductPurchase();
			ecoPurchase.setBuyer(buyer);
			ecoPurchase.setEcoProduct(ecoProduct);
			ecoPurchase.setPurchaseDate(LocalDateTime.now());
			ecoPurchase.setDeliveryAddress(deliveryAddress); // 요청된 주소를 저장
			ecoPurchase.setStatus(EcoProductStatus.RESERVED);
			ecoPurchase.setEcoProductTitle(ecoProduct.getTitle());

			// Save the purchase
			ecoProductService.savePurchase(ecoPurchase);

			// 구매자 포인트 차감
			buyer.setEco_point(buyer.getEco_point() - ecoProduct.getPrice());
			memberService.saveMember(buyer);

			// DTO 생성 및 응답
			EcoProductPurchaseDTO ecoDto = EcoProductPurchaseDTO.fromEntity(ecoPurchase);
			return ResponseEntity.ok(ecoDto); // 구매 성공 시 DTO 반환
		} catch (Exception e) {
			log.error("Error during purchase", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	@GetMapping("/history/{member_id}")
	public ResponseEntity<List<EcoProductPurchaseHistoryDTO>> getEcoProductPurchaseHistory(
			@PathVariable("member_Id") String memberId) {
		// 해당 ID의 사용자가 존재하는지 확인
		Optional<Member> memberOpt = memberService.findById(memberId);
		if (!memberOpt.isPresent()) {
			return ResponseEntity.badRequest().body(null); // 사용자 존재하지 않음
		}

		// 사용자 ID로 구매 내역 조회
		List<EcoProductPurchase> purchaseHistory = ecoProductService.getPurchaseHistoryByMemberId(memberId);
	    List<EcoProductPurchaseHistoryDTO> purchaseHistoryDTOs = purchaseHistory.stream()
	            .map(EcoProductPurchaseHistoryDTO::fromEntity)
	            .collect(Collectors.toList());
		return ResponseEntity.ok(purchaseHistoryDTOs);
	}

	// 전체내역
	@GetMapping("/allHistory")
	public ResponseEntity<List<EcoProductPurchaseDTO>> getAllEcoProductPurchases() {
		List<EcoProductPurchase> purchaseHistory = ecoProductService.getAllEcoProductPurchases();
		if (purchaseHistory.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		List<EcoProductPurchaseDTO> dtoList = purchaseHistory.stream().map(EcoProductPurchaseDTO::fromEntity).toList();
		return ResponseEntity.ok(dtoList);
	}

	@PutMapping("/updateStatus/{productId}")
	public ResponseEntity<String> updateEcoProductStatus(@PathVariable("id") Long id,
			@RequestParam("status") EcoProductStatus status) {
		try {
			Optional<EcoProduct> ecoProductOpt = ecoProductService.findById(id);
			if (!ecoProductOpt.isPresent()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found.");
			}

			EcoProductPurchase ecoPurchase = new EcoProductPurchase();
			EcoProduct ecoProduct = ecoProductOpt.get();
			ecoPurchase.setStatus(status);
			ecoProductService.save(ecoProduct);

			return ResponseEntity.ok("Product status updated to " + status.getDescription());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating product status.");
		}
	}

}
