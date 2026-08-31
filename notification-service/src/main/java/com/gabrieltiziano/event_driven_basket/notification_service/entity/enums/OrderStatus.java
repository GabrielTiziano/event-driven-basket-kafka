package com.gabrieltiziano.event_driven_basket.notification_service.entity.enums;

public enum OrderStatus {
    CREATED("Pedido criado"),
    PAID("Pedido pago"),
    SHIPPED("Pedido enviado"),
    DELIVERED("Pedido entregue"),
    CANCELLED("Pedido cancelado");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
