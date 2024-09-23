package com.example.board.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.example.board.model.ecoProduct.EcoProduct;
import com.example.board.model.ecoProduct.EcoProductImage;
import com.example.board.model.product.Category;
import com.example.board.model.product.Image;
import com.example.board.model.product.Product;

import lombok.Data;

@Data
public class EcoProductDTO {
	private Long ecoProductId; 
    private String title;
    private Long price; 
    private String content;
    private List<String> imageUrls;
    
    public static EcoProductDTO fromEntity(EcoProduct ecoproduct) {
    	EcoProductDTO dto = new EcoProductDTO();
    	dto.setEcoProductId(ecoproduct.getEcoProductId());
    	dto.setContent(ecoproduct.getContent());
    	dto.setPrice(ecoproduct.getPrice());
    	dto.setTitle(ecoproduct.getTitle());
    	dto.setImageUrls(ecoproduct.getEcoProductImages().stream()
                .map(EcoProductImage::getUrl)
                .collect(Collectors.toList()));
    	return dto;
    }
}
