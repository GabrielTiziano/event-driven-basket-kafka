package com.gabrieltiziano.event_driven_basket.order_service.controller;

import com.gabrieltiziano.event_driven_basket.order_service.dto.request.CreateOrderRequest;
import com.gabrieltiziano.event_driven_basket.order_service.dto.response.OrderResponse;
import com.gabrieltiziano.event_driven_basket.order_service.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(request));
    }

    public void updateOrder(){

    }
}
