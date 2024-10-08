package com.example.board.model.ecoProduct;

public enum EcoProductStatus {
    
    RESERVED("구매신청"),
    COMPLETED("발송완료");

    private final String description;

    EcoProductStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
 }
