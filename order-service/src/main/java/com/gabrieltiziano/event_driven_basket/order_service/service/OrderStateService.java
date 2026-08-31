package com.gabrieltiziano.event_driven_basket.order_service.service;

import com.gabrieltiziano.event_driven_basket.order_service.entity.enums.OrderEvent;
import com.gabrieltiziano.event_driven_basket.order_service.entity.enums.OrderStatus;
import com.gabrieltiziano.event_driven_basket.order_service.exception.InvalidOrderTransitionException;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.StateMachineEventResult;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class OrderStateService {
    private final StateMachine<OrderStatus, OrderEvent> stateMachine;

    public OrderStateService(StateMachine<OrderStatus, OrderEvent> stateMachine) {
        this.stateMachine = stateMachine;
    }

    public OrderStatus processEvent(OrderStatus currentStatus, OrderEvent event) {
        stateMachine.stopReactively().block();

        stateMachine.getStateMachineAccessor()
                .doWithAllRegions(access ->
                        access.resetStateMachineReactively(
                                new DefaultStateMachineContext<>(currentStatus, null, null, null)
                        ).block());

        stateMachine.startReactively().block();

        Message<OrderEvent> message = MessageBuilder.withPayload(event).build();

        StateMachineEventResult<OrderStatus, OrderEvent> result =
                stateMachine.sendEvent(Mono.just(message)).blockLast();

        boolean accepted = result != null
                && result.getResultType() == StateMachineEventResult.ResultType.ACCEPTED;

        if (accepted) {
            return stateMachine.getState().getId();
        }

        throw new InvalidOrderTransitionException(
                "Não é possível aplicar o evento " + event + " a partir do estado " + currentStatus);
    }
}
