package com.example.board.model.product;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter @ToString
public class ProductWriteForm {
	
	@Size(min = 2, max = 30)
	private String title;				// 상품 제목
	
	@Size(min = 4)
	private String contents;			// 상품 내용

	private Long price;					// 상품 가격

	private List<String> productImages;  // 이미지 목록 추가
	
	private Category category;        // 상품 카테고리
	private String member_id; // 회원 ID 추가
	
	public static Product toProduct(ProductWriteForm productWriteForm) {
	    Product product = new Product();
	    product.setTitle(productWriteForm.getTitle());
	    product.setContents(productWriteForm.getContents());
	    product.setPrice(productWriteForm.getPrice());
	    product.setHit(0L);
	    product.setHeart(0L);
	    product.setCreatedTime(LocalDateTime.now());
	    product.setCategory(productWriteForm.getCategory());
	    return product;
	}

}
