package com.example.board.dto;

import com.example.board.model.product.Category;
import com.example.board.model.product.Image;
import com.example.board.model.product.Product;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class ProductDTO {
    private Long productId;
    private String title;
    private String contents;
    private Long price;
    private Long hit;
    private Long heart;
    private LocalDateTime createdTime;
    private List<String> imageUrls;
    private Category category;
    private String seller;
    public static ProductDTO fromEntity(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setProductId(product.getId());
        dto.setTitle(product.getTitle());
        dto.setContents(product.getContents());
        dto.setPrice(product.getPrice());
        dto.setHit(product.getHit());
        dto.setHeart(product.getHeart());
        dto.setCreatedTime(product.getCreated_time());
        dto.setImageUrls(product.getProductImages().stream()
            .map(Image::getUrl)
            .collect(Collectors.toList()));
        dto.setCategory(product.getCategory());
        dto.setSeller(product.getMember().getName());
        return dto;
    }
}