package dev.tschumin.ordermanagement.order.application.command;

import dev.tschumin.ordermanagement.order.domain.valueobject.OrderId;

/**
 * Команда отмены заказа.
 */
public class CancelOrderCommand {

    /**
     * Идентификатор отменяемого заказа.
     */
    private OrderId orderId;
}
