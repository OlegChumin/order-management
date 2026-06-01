package dev.tschumin.ordermanagement.order.domain.valueobject;

import dev.tschumin.ordermanagement.order.domain.exception.OrderDomainException;

/**
 * Количество товара.
 *
 * @param value значение количества товара
 */
public record Quantity(int value) {

    /**
     * Создает количество товара.
     *
     * @param value значение количества товара
     */
    public Quantity {
        if (value <= 0) {
            throw new OrderDomainException("Количество товара должно быть больше нуля");
        }
    }
}
