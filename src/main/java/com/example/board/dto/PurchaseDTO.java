package com.example.board.dto;

import java.time.LocalDateTime;

import com.example.board.model.chat.ChatRoom;
import com.example.board.model.member.Member;
import com.example.board.model.member.ProfileImage;
import com.example.board.model.product.Purchase;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class PurchaseDTO {
//	private String member_id;
	private Long id;
	private Long product;
	private String productTitle;
	private LocalDateTime purchaseDate;

	private String sellerName; // 판매자의 닉네임
	private String sellerProfileImage; // 판매자의 프로필 이미지

	public static PurchaseDTO fromEntity(Purchase purchase) {
		PurchaseDTO dto = new PurchaseDTO();
		dto.setId(purchase.getId());
		dto.setProduct(purchase.getProduct().getId());
		dto.setProductTitle(purchase.getProduct().getTitle());
		dto.setPurchaseDate(purchase.getPurchaseDate());

		Member seller = purchase.getProduct().getMember(); // Product 객체에서 판매자 정보(Member)를 가져옴
		dto.setSellerName(seller.getName()); // 판매자의 닉네임 설정
		
		// 프로필 이미지 설정 (ProfileImage 객체에서 경로 추출)
		ProfileImage profileImage = seller.getProfileImage();
		if (profileImage != null) {
			dto.setSellerProfileImage(profileImage.getUrl()); // 이미지 URL 설정
		} else {
			dto.setSellerProfileImage(null); // 프로필 이미지가 없을 경우 null 처리
		}
		return dto;
	}
}
