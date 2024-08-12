package com.example.board.model.product;

import java.time.LocalDateTime;

import com.example.board.model.member.Member;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class ProductUpdateForm {
	private Long product_id;					// 상품 아이디
	
	@NotBlank
	private String title;				// 상품 제목
	@NotBlank
	private String contents;			// 상품 내용
	
	private Member member;				// Member 클래스 만들어지면 구동
	
	private Long price;					// 상품 가격
	private Long hit;				    // 상품 조회수
	private Long heart;					// 상품 좋아요 수
	private LocalDateTime created_time; // 상품 작성일
	private boolean fileRemoved;
	
	public static Product toProduct(ProductUpdateForm productUpdateForm) {
		Product product = new Product();
		
		product.setProduct_id(productUpdateForm.getProduct_id());
		product.setTitle(productUpdateForm.getTitle());
		product.setContents(productUpdateForm.getContents());
		product.setPrice(productUpdateForm.getPrice());
		product.setMember(productUpdateForm.getMember());
		product.setHit(productUpdateForm.getHit());
		product.setCreated_time(productUpdateForm.getCreated_time());
		
		return product;
	}
	
}
