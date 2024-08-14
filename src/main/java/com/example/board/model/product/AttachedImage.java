package com.example.board.model.product;

import jakarta.persistence.Entity;
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

    @ManyToOne
    @JoinColumn(name="product_id")
    private Product product;
    
    private String original_image;
    private String saved_image;
    private Long image_size;

    public AttachedImage(String original_image, String saved_image, Long image_size) {
        this.original_image = original_image;
        this.saved_image = saved_image;
        this.image_size = image_size;
    }
}
