package com.example.board.model.product;

import java.time.LocalDateTime;

import com.example.board.model.ecoProduct.EcoProduct;
import com.example.board.model.member.Member;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Purchase {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "member_id")
	private Member buyer;

	@ManyToOne
	@JoinColumn(name = "product_id")
	private Product product;
	
	@ManyToOne
	@JoinColumn(name = "ecoProductId")
	private EcoProduct ecoProduct;

	@JoinColumn(name = "title")
	private String ProductTitle;

	private LocalDateTime purchaseDate;

	// 판매자의 닉네임과 프로필 이미지 추가
	@Transient // 이 필드는 데이터베이스에 저장되지 않도록 설정
	private String sellerName;

	@Transient
	private String sellerProfileImage;

	// 판매자 정보를 설정하는 메서드
//	public void setSellerInfo(Member seller) {
//		if (seller != null) {
//			this.sellerName = seller.getName(); // 판매자의 닉네임
//			ProfileImage profileImage = seller.getProfileImage();
//			this.sellerProfileImage = (profileImage != null) ? profileImage.getUrl() : null; // 프로필 이미지 URL
//		}
//	}
}