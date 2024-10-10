package com.example.board.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.example.board.model.ecoProduct.EcoProductImage;
import com.example.board.model.ecoProduct.EcoProductPurchase;
import com.example.board.model.ecoProduct.EcoProductStatus;

import lombok.Data;

@Data
public class EcoProductPurchaseHistoryDTO {
	private String title;
	private String address;
	private String imageUrl;
	private EcoProductStatus status;
	private LocalDateTime buyDate;
	
	public static EcoProductPurchaseHistoryDTO fromEntity(EcoProductPurchase ecoPurchase) {
		EcoProductPurchaseHistoryDTO ecoDto = new EcoProductPurchaseHistoryDTO();
		ecoDto.setTitle(ecoPurchase.getEcoProduct().getTitle());
		ecoDto.setBuyDate(ecoPurchase.getPurchaseDate());
		ecoDto.setAddress(ecoPurchase.getDeliveryAddress());
		ecoDto.setStatus(ecoPurchase.getStatus());
		// EcoProduct의 이미지 중 첫 번째 이미지를 가져와서 DTO에 추가
		List<EcoProductImage> images = ecoPurchase.getEcoProduct().getEcoProductImages();
		if (!images.isEmpty()) {
			ecoDto.setImageUrl(images.get(0).getUrl());
		}

		return ecoDto;
	}
}
