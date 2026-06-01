package dev.tschumin.ordermanagement.order.application.exception;

import dev.tschumin.ordermanagement.order.domain.valueobject.OrderId;

import java.util.Objects;

/**
 * Исключение, возникающее при отсутствии заказа в хранилище.
 */
public class OrderNotFoundException extends RuntimeException {

    /**
     * Идентификатор заказа, который не был найден.
     */
    private final OrderId orderId;

    /**
     * Создает исключение отсутствующего заказа.
     *
     * @param orderId идентификатор заказа, который не был найден
     */
    public OrderNotFoundException(OrderId orderId) {
        super("Заказ не найден: " + Objects.requireNonNull(orderId, "Идентификатор заказа не может быть null").value());
        this.orderId = orderId;
    }

    /**
     * Возвращает идентификатор заказа, который не был найден.
     *
     * @return идентификатор заказа
     */
    public OrderId orderId() {
        return orderId;
    }
}
