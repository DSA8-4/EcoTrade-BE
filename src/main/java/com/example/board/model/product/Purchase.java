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
	
	@JoinColumn(name = "title")
	private String ProductTitle;

	private LocalDateTime purchaseDate;
	
	private String deliveryAddress;
	
	private String status;

}
