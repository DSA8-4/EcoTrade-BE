package com.example.board.model.product;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Primary key for the ProductImage entity

    private String url; // URL of the image

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    @JsonBackReference
    private Product product; // Reference back to the Product entity

    public Image() {}

    public Image(String url, Product product) {
        this.url = url;
        this.product = product;
    }
}
