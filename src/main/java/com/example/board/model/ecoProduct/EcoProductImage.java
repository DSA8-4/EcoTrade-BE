package com.example.board.model.ecoProduct;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class EcoProductImage {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Primary key for the ProductImage entity

    @Column(length = 1024)
    private String url; // URL of the image

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ecoProductId")
    @JsonBackReference
    private EcoProduct ecoProduct; // Reference back to the Product entity

    public EcoProductImage() {}

    public EcoProductImage(String url, EcoProduct ecoProduct) {
        this.url = url;
        this.ecoProduct = ecoProduct;
    }
}
