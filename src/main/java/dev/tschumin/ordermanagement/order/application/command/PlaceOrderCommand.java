package dev.tschumin.ordermanagement.order.application.command;

import dev.tschumin.ordermanagement.order.domain.valueobject.CustomerId;

/**
 * Команда размещения заказа.
 */
public class PlaceOrderCommand {

    /**
     * Идентификатор клиента, размещающего заказ.
     */
    private CustomerId customerId;
}
