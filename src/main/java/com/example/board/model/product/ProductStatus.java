package com.example.board.model.product;

public enum ProductStatus {
    TRADING("거래중"),
    RESERVED("거래예약"),
    COMPLETED("거래완료");

    private final String description;

    ProductStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
