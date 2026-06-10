package dev.tschumin.ordermanagement.order.application.command;

import dev.tschumin.ordermanagement.order.domain.valueobject.OrderId;

import java.util.Objects;

/**
 * Команда отмены заказа.
 */
public class CancelOrderCommand {

    /**
     * Идентификатор отменяемого заказа.
     */
    private final OrderId orderId;

    /**
     * Создает команду отмены заказа.
     *
     * @param orderId идентификатор отменяемого заказа
     */
    public CancelOrderCommand(OrderId orderId) {
        this.orderId = Objects.requireNonNull(orderId, "Идентификатор заказа не может быть null");
    }

    /**
     * Возвращает идентификатор отменяемого заказа.
     *
     * @return идентификатор отменяемого заказа
     */
    public OrderId orderId() {
        return orderId;
    }
}
