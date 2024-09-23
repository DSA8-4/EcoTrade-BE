package com.example.board.model.ecoProduct;

import java.util.List;

import com.example.board.model.product.Image;
import com.example.board.model.product.Product;
import com.example.board.model.product.ProductStatus;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Data
@Entity
public class EcoProduct {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ecoProductId; 

    private String title;
    private Long price; 
    private String content; 
    
    @Enumerated(EnumType.STRING)
	private ProductStatus status; // 거래 상태 필드 추가
    
    @OneToMany(mappedBy = "ecoProduct", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	private List<EcoProductImage> ecoProductImages;
}
