package dev.tschumin.ordermanagement.order.domain.model;

import dev.tschumin.ordermanagement.order.domain.valueobject.Money;
import dev.tschumin.ordermanagement.order.domain.valueobject.ProductId;
import dev.tschumin.ordermanagement.order.domain.valueobject.Quantity;

/**
 * Строка заказа с товаром, количеством и ценой.
 */
public class OrderLine {

    /**
     * Идентификатор товара в строке заказа.
     */
    private ProductId productId;

    /**
     * Количество товара в строке заказа.
     */
    private Quantity quantity;

    /**
     * Цена единицы товара.
     */
    private Money unitPrice;
}
