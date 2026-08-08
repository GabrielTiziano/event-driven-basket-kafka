package com.gabrieltiziano.event_driven_basket.order_service.config;

import com.gabrieltiziano.event_driven_basket.order_service.entity.enums.OrderEvent;
import com.gabrieltiziano.event_driven_basket.order_service.entity.enums.OrderStatus;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

@Configuration
@EnableStateMachine
public class OrderStateMachineConfig extends StateMachineConfigurerAdapter<OrderStatus, OrderEvent> {
    @Override
    public void configure(StateMachineStateConfigurer<OrderStatus, OrderEvent> states) throws Exception {
        states
                .withStates()
                .initial(OrderStatus.CREATED)
                .state(OrderStatus.PAID)
                .state(OrderStatus.SHIPPED)
                .state(OrderStatus.DELIVERED)
                .end(OrderStatus.CANCELLED);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<OrderStatus, OrderEvent> transitions) throws Exception {
        transitions
                .withExternal()
                .source(OrderStatus.CREATED).target(OrderStatus.PAID).event(OrderEvent.PAY)
                .and()
                .withExternal()
                .source(OrderStatus.PAID).target(OrderStatus.SHIPPED).event(OrderEvent.SHIP)
                .and()
                .withExternal()
                .source(OrderStatus.SHIPPED).target(OrderStatus.DELIVERED).event(OrderEvent.DELIVER)
                .and()
                .withExternal()
                .source(OrderStatus.CREATED).target(OrderStatus.CANCELLED).event(OrderEvent.CANCEL)
                .and()
                .withExternal()
                .source(OrderStatus.PAID).target(OrderStatus.CANCELLED).event(OrderEvent.CANCEL);
    }
}
