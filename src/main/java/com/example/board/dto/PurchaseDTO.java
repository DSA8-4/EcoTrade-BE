package com.example.board.dto;

import java.time.LocalDateTime;

import com.example.board.model.chat.ChatRoom;
import com.example.board.model.member.Member;
import com.example.board.model.product.Product;
import com.example.board.model.product.ProductStatus;
import com.example.board.model.product.Purchase;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class PurchaseDTO {
//	private String member_id;
	private Long id;
	private Long product;
	private String productTitle;
	private LocalDateTime purchaseDate;

	private String sellerName; // 판매자의 닉네임
	private String sellerProfileImage; // 판매자의 프로필 이미지
	private String status;

	public static PurchaseDTO fromEntity(Purchase purchase) {
		PurchaseDTO dto = new PurchaseDTO();
		dto.setId(purchase.getId());
		dto.setProduct(purchase.getProduct().getId());
		dto.setProductTitle(purchase.getProduct().getTitle());
		dto.setPurchaseDate(purchase.getPurchaseDate());

		Member seller = purchase.getProduct().getMember(); // Product 객체에서 판매자 정보(Member)를 가져옴
		dto.setSellerName(seller.getName()); // 판매자의 닉네임 설정

		dto.setSellerProfileImage(seller.getProfileImageUrl());
		// Product의 상태를 DTO로 설정
	    ProductStatus productStatus = purchase.getProduct().getStatus();
	    if (productStatus != null) {
	        dto.setStatus(productStatus.name()); // Enum을 문자열로 변환
	    } else {
	        dto.setStatus("Unknown"); // 상태가 null일 경우 기본값 설정
	    }

		return dto;
	}
}
