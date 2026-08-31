package com.gabrieltiziano.event_driven_basket.notification_service.entity.enums;

public enum PaymentMethod {
    PIX("Pix"),
    DEBIT("Cartão de débito"),
    CREDIT("Cartão de crédito");

    private final String description;

    PaymentMethod(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
