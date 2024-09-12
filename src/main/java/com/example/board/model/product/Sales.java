package com.example.board.model.product;

import java.time.LocalDateTime;

import com.example.board.model.member.Member;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Sales {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "member_id")
	private Member seller; //판매자 정보
	
	
	@ManyToOne
    @JoinColumn(name = "product_id")
	private Product product; //판매된 상품 정보
	
	private LocalDateTime salesDate; //판매일자
	
	

}
