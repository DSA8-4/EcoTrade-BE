package com.example.board.dto;

import java.time.LocalDateTime;

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
	
	public static EcoProductPurchaseDTO fromEntity(EcoProductPurchase ecoPurchase) {
		EcoProductPurchaseDTO ecoDto = new EcoProductPurchaseDTO();
		ecoDto.setId(ecoPurchase.getId());
		ecoDto.setEcoProduct(ecoPurchase.getEcoProduct().getEcoProductId());
		ecoDto.setProductTitle(ecoPurchase.getEcoProduct().getTitle());
		ecoDto.setPurchaseDate(ecoPurchase.getPurchaseDate());


		return ecoDto;
	}
}
