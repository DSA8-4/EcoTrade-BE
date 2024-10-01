package com.example.board.dto;

import java.time.LocalDateTime;

import com.example.board.model.product.ProductStatus;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter @Setter
public class SalesDTO {
//	private String member_id;
	  private Long id;
	    private String title;
	    private String contents;
	    private Long price;
	    private LocalDateTime createdTime;
	    private String sellerName; // 판매자의 닉네임 추가
	    private String sellerProfileImage; // 판매자의 프로필 이미지 추가
	    private String status;
	    private String productImageUrl; // 상품 이미지 URL 추가
	    

}
