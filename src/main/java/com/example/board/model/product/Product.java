package com.example.board.model.product;

import com.example.board.model.chat.ChatRoom;
import com.example.board.model.member.Area;
import com.example.board.model.member.Member;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; // 상품 아이디

	private String title; // 상품 제목
	private String contents; // 상품 내용

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	@JsonBackReference
	private Member member; // 작성자 정보 (회원)

	private Long price; // 상품 가격
	private Long hit = 0L; // 상품 조회수 초기값 설정
	private Long heart = 0L; // 상품 좋아요 수 초기값 설정
//    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Like> likes = new ArrayList<>();
	@Column(name = "created_time", nullable = false)
	private LocalDateTime createdTime; // 상품 작성일

	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	private List<Image> productImages; // 이미지 목록 추가

	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	private List<ChatRoom> chatRooms; // ChatRoom 목록 추가

	@Enumerated(EnumType.STRING)
	private Category category; // 상품 카테고리

	@Enumerated(EnumType.STRING)
	private ProductStatus status; // 거래 상태 필드 추가

	public Product() {
		this.status = ProductStatus.TRADING; // 기본 상태를 '거래중'으로 설정
	}
	
	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductLike> productLikes; // This assumes you have a ProductLike entity

    // Getter for productLikes
    public List<ProductLike> getProductLikes() {
        return productLikes;
    }
    
	// 조회수 증가 메서드
	public void addHit() {
		this.hit++;
	}

	public void addHeart() {
		this.heart++;
	}

	public void removeHeart() {
		if (this.heart > 0) { // 하트 수가 0보다 클 때만 감소
			this.heart--;
		}
	}
}
