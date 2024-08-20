package com.example.board.model.product;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter @Setter
@NoArgsConstructor
@ToString(exclude = "product") // product 필드를 toString()에서 제외
public class AttachedImage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long attachedImage_id;
    
    private String Original_image;
    private Long Image_size;
    private String Saved_image;
    private String fileName;
    private String filePath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    @JoinColumn(name = "product_id")
    private Product product;
}
