package dev.tschumin.ordermanagement.order.domain.valueobject;

import java.util.Objects;
import java.util.UUID;

/**
 * Идентификатор заказа.
 *
 * @param value значение идентификатора заказа
 */
public record OrderId(UUID value) {

    /**
     * Создает идентификатор заказа.
     *
     * @param value значение идентификатора заказа
     */
    public OrderId {
        Objects.requireNonNull(value, "Идентификатор заказа не может быть null");
    }

    /**
     * Создает новый случайный идентификатор заказа.
     *
     * @return новый идентификатор заказа
     */
    public static OrderId newId() {
        return new OrderId(UUID.randomUUID());
    }
}
