package com.example.board.model.product;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class ProductWriteForm {
	
	@Size(min = 2, max = 30)
	private String title;				// 상품 제목
	
	@Size(min = 4)
	private String contents;			// 상품 내용
	
	@NotNull
	private Long price;					// 상품 가격
	
	public static Product toProduct(ProductWriteForm productWriteForm) {
		Product product = new Product();
		
		product.setTitle(productWriteForm.getTitle());
		product.setContents(productWriteForm.getContents());
		product.setPrice(productWriteForm.getPrice());
		product.setHit(0L);
		product.setHeart(0L);
		product.setCreated_time(LocalDateTime.now());
		
		return product;
	}
}
