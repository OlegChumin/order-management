package dev.tschumin.ordermanagement.order.domain.event;

import dev.tschumin.ordermanagement.order.domain.valueobject.OrderId;

import java.time.Instant;
import java.util.Objects;

/**
 * Доменное событие размещения заказа.
 *
 * @param orderId идентификатор размещенного заказа
 * @param occurredAt момент возникновения события
 */
public record OrderPlacedEvent(OrderId orderId, Instant occurredAt) implements DomainEvent {

    /**
     * Создает доменное событие размещения заказа.
     *
     * @param orderId идентификатор размещенного заказа
     * @param occurredAt момент возникновения события
     */
    public OrderPlacedEvent {
        Objects.requireNonNull(orderId, "Идентификатор заказа в событии не может быть null");
        Objects.requireNonNull(occurredAt, "Момент события не может быть null");
    }
}
