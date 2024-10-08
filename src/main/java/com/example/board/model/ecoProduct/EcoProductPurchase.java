package com.example.board.model.ecoProduct;

import java.time.LocalDateTime;

import com.example.board.model.member.Member;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class EcoProductPurchase {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "member_id")
	private Member buyer;
	
	@ManyToOne
	@JoinColumn(name = "ecoProductId")
	private EcoProduct ecoProduct;
	
	@JoinColumn(name = "title")
	private String ecoProductTitle;
	
	private LocalDateTime purchaseDate;
	
	private String deliveryAddress;
	

	@Enumerated(EnumType.STRING)
	private EcoProductStatus status;

  @Override
  public String toString() {
      return "EcoProductPurchase{" +
              "id=" + id +
              ", ProductTitle='" + ecoProductTitle + '\'' +
              ", purchaseDate=" + purchaseDate +
              '}';
  }
}
