package dev.tschumin.ordermanagement.order.domain.event;

import dev.tschumin.ordermanagement.order.domain.valueobject.OrderId;

import java.time.Instant;

/**
 * Доменное событие размещения заказа.
 */
public class OrderPlacedEvent implements DomainEvent {

    /**
     * Идентификатор размещенного заказа.
     */
    private OrderId orderId;

    /**
     * Момент возникновения события.
     */
    private Instant occurredAt;
}
