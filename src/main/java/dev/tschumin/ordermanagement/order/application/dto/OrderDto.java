package dev.tschumin.ordermanagement.order.application.dto;

import dev.tschumin.ordermanagement.order.domain.valueobject.OrderId;
import dev.tschumin.ordermanagement.order.domain.valueobject.OrderStatus;

/**
 * DTO представления заказа для внешних слоев приложения.
 */
public class OrderDto {

    /**
     * Идентификатор заказа.
     */
    private OrderId id;

    /**
     * Статус заказа.
     */
    private OrderStatus status;
}
