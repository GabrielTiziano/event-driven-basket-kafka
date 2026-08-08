package com.gabrieltiziano.event_driven_basket.order_service.repository;

import com.gabrieltiziano.event_driven_basket.order_service.entity.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderRepository extends MongoRepository<Order, String> {
}
