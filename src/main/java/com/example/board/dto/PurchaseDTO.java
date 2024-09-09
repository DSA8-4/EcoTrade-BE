package com.example.board.dto;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter @Setter
public class PurchaseDTO {
	private Long id;
    private Long productId;
    private String productTitle;
    private LocalDateTime purchaseDate;
}
