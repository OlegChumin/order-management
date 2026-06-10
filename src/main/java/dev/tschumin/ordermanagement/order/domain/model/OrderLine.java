package dev.tschumin.ordermanagement.order.domain.model;

import dev.tschumin.ordermanagement.order.domain.valueobject.Money;
import dev.tschumin.ordermanagement.order.domain.valueobject.ProductId;
import dev.tschumin.ordermanagement.order.domain.valueobject.Quantity;

import java.util.Objects;

/**
 * Строка заказа с товаром, количеством и ценой.
 *
 * @param productId идентификатор товара в строке заказа
 * @param quantity количество товара в строке заказа
 * @param unitPrice цена единицы товара
 */
public record OrderLine(ProductId productId, Quantity quantity, Money unitPrice) {

    /**
     * Создает строку заказа.
     *
     * @param productId идентификатор товара в строке заказа
     * @param quantity количество товара в строке заказа
     * @param unitPrice цена единицы товара
     */
    public OrderLine {
        Objects.requireNonNull(productId, "Идентификатор товара не может быть null");
        Objects.requireNonNull(quantity, "Количество товара не может быть null");
        Objects.requireNonNull(unitPrice, "Цена единицы товара не может быть null");
    }

    /**
     * Рассчитывает стоимость строки заказа.
     *
     * @return стоимость строки заказа
     */
    public Money totalPrice() {
        return unitPrice.multiply(quantity);
    }
}
