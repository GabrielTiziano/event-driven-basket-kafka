package com.gabrieltiziano.event_driven_basket.order_service.exception;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(String id) {
        super("Pedido não encontrado com id: " + id);
    }
}