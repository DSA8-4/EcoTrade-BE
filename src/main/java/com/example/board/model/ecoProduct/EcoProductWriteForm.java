package com.example.board.model.ecoProduct;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
@Getter
@Setter
@ToString
public class EcoProductWriteForm {
    @Size(min = 2, max = 30)
    private String title;				// 상품 제목

    @Size(min = 4)
    private String content;			// 상품 내용

    private Long ecoPoints;					// 상품 가격

    private List<String> ecoProductImages;  // 이미지 목록 추가

    public static EcoProduct toEcoProduct(EcoProductWriteForm ecoProductWriteForm) {
        EcoProduct ecoProduct = new EcoProduct();

        ecoProduct.setTitle(ecoProductWriteForm.getTitle());
        ecoProduct.setContent(ecoProductWriteForm.getContent());
        ecoProduct.setPrice(ecoProductWriteForm.getEcoPoints());
//        ecoProduct.setEcoProductImages(ecoProduct.getEcoProductImages());

        return ecoProduct;
    }
}
