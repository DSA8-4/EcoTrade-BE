package com.example.board.model.product;
import java.time.LocalDateTime;

import com.example.board.model.member.Member;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class Product {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long product_id;					// 상품 아이디
	
	private String title;				// 상품 제목
	private String contents;			// 상품 내용
	
	@ManyToOne
	@JoinColumn(name = "member_id")
	private Member member;					// Member 클래스 만들어지면 구동
	
	private Long price;					// 상품 가격
	private Long hit;				    // 상품 조회수
	private Long good;					// 상품 좋아요 수
	private LocalDateTime created_time; // 상품 작성일
	
	public void addHit() {
		this.hit++;
	}
}