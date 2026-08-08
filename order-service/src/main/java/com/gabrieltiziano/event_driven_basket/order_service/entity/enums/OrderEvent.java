package com.gabrieltiziano.event_driven_basket.order_service.entity.enums;

public enum OrderEvent {
    CREATE("Cria o pedido"),
    PAY("Confirma o pagamento"),
    SHIP("Despacha o pedido"),
    DELIVER("Confirma a entrega"),
    CANCEL("Cancela o pedido");

    private final String description;

    OrderEvent(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
