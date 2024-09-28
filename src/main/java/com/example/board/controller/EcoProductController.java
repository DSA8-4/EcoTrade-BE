package com.example.board.controller;

import com.example.board.dto.EcoProductDTO;
import com.example.board.model.ecoProduct.EcoProduct;
import com.example.board.model.ecoProduct.EcoProductImage;
import com.example.board.model.ecoProduct.EcoProductWriteForm;
import com.example.board.model.member.Member;
import com.example.board.model.product.Purchase;
import com.example.board.service.EcoProductService;
import com.example.board.service.MemberService;
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
@RequestMapping("/EcoProduct")
@RequiredArgsConstructor
public class EcoProductController {
	private final EcoProductService ecoProductService;
	private final MemberService memberService;
	private final JwtTokenProvider jwtTokenProvider;

	// 에코포인트로만 구매 가능한 상품 등록
	@PostMapping("/register")
	public ResponseEntity<String> registerEcoPointOnlyProduct(
			@RequestBody EcoProductWriteForm ecoProductWriteForm) {
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
			@RequestParam(name = "title", required = false) String title,
			@RequestParam(name = "contents", required = false) String contents,
			@RequestParam(name = "ecoPoints", required = false) Long ecoPoints,
			@RequestParam(name = "imageUrls", required = false) List<String> imageUrls) {

		log.info("Updating eco-point only product with ID: {}", id);

		try {
			// 상품 조회
			Optional<EcoProduct> optionalEcoProduct = ecoProductService.findById(id);
			if (optionalEcoProduct.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found with ID: " + id);
			}

			EcoProduct ecoProduct = optionalEcoProduct.get();

			// Update title, content, and ecoPoints if provided
			if (title != null) {
				ecoProduct.setTitle(title);
			}
			if (contents != null) {
				ecoProduct.setContent(contents);
			}
			if (ecoPoints != null) {
				if (ecoPoints < 0) {
					return ResponseEntity.badRequest().body("Eco points cannot be negative.");
				}
				ecoProduct.setPrice(ecoPoints); // Update ecoPoints
			}

			// Handle image updates
			if (imageUrls != null && !imageUrls.isEmpty()) {
				// Remove old images
				ecoProduct.getEcoProductImages().clear();

				// Add new images
				for (String url : imageUrls) {
					EcoProductImage ecoProductImage = new EcoProductImage();
					ecoProductImage.setUrl(url); // Assume EcoProductImage has a setUrl method
					ecoProductImage.setEcoProduct(ecoProduct); // Set reference back to EcoProduct
					ecoProduct.getEcoProductImages().add(ecoProductImage);
				}
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

	@PostMapping("/purchase/{id}")
	public ResponseEntity<String> purchaseEcoProduct(@PathVariable("id") Long id,
			@RequestHeader("Authorization") String authorizationHeader) {
		try {
			log.info("Extracting token...");
			// Extract the JWT token and validate it
			String token = authorizationHeader.replace("Bearer ", "");
			if (!jwtTokenProvider.validateToken(token)) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token.");
			}

			log.info("Extracting member ID from token...");
			// Get the memberId from the token
			String memberId = jwtTokenProvider.getUserIdFromToken(token);

			log.info("Finding product...");
			// Find the EcoProduct
			Optional<EcoProduct> ecoProductOpt = ecoProductService.findById(id);
			if (!ecoProductOpt.isPresent()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found.");
			}

			EcoProduct ecoProduct = ecoProductOpt.get();

			log.info("Creating new purchase...");
			// Create a new purchase
			Purchase purchase = new Purchase();
			Member buyer = memberService.findMemberById(memberId); // Get the buyer (member)
			purchase.setBuyer(buyer);
			purchase.setEcoProduct(ecoProduct);
			purchase.setPurchaseDate(LocalDateTime.now());

			// Save the purchase
			ecoProductService.savePurchase(purchase);

			log.info("Updating member's eco points...");

			// 구매자 포인트 차감
			if (buyer.getEco_point() < ecoProduct.getPrice()) {
				// 포인트 부족으로 인한 예외 처리
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body("Eco points are insufficient to complete the purchase.");
			}

			// 구매 가능 - 포인트 차감
			buyer.setEco_point(buyer.getEco_point() - ecoProduct.getPrice());
			memberService.saveMember(buyer);

			return ResponseEntity.ok("ecoProduct purchased successfully.");
		} catch (Exception e) {
			log.error("Error during purchase", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during purchase.");
		}
	}
}
