package com.example.board.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.example.board.model.ecoProduct.EcoProductImage;
import com.example.board.model.ecoProduct.EcoProductPurchase;
import com.example.board.model.member.Member;
import com.example.board.model.product.Purchase;

import lombok.Data;

@Data
public class EcoProductPurchaseDTO {
	private Long id;
	private Long ecoProduct;
	private String productTitle;
	private LocalDateTime purchaseDate;
	private String productImageUrl; // 하나의 이미지 URL 추가
	private String memberId;

	public static EcoProductPurchaseDTO fromEntity(EcoProductPurchase ecoPurchase) {
		EcoProductPurchaseDTO ecoDto = new EcoProductPurchaseDTO();
		ecoDto.setId(ecoPurchase.getId());
		ecoDto.setEcoProduct(ecoPurchase.getEcoProduct().getEcoProductId());
		ecoDto.setProductTitle(ecoPurchase.getEcoProduct().getTitle());
		ecoDto.setPurchaseDate(ecoPurchase.getPurchaseDate());

		ecoDto.setMemberId(ecoPurchase.getBuyer().getMember_id());

		// EcoProduct의 이미지 중 첫 번째 이미지를 가져와서 DTO에 추가
		List<EcoProductImage> images = ecoPurchase.getEcoProduct().getEcoProductImages();
		if (!images.isEmpty()) {
			ecoDto.setProductImageUrl(images.get(0).getUrl()); // 첫 번째 이미지 URL 설정
		}

		return ecoDto;
	}
}
