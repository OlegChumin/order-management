package dev.tschumin.ordermanagement.order.application.command;

import dev.tschumin.ordermanagement.order.domain.valueobject.Money;
import dev.tschumin.ordermanagement.order.domain.valueobject.ProductId;
import dev.tschumin.ordermanagement.order.domain.valueobject.Quantity;

import java.util.Objects;

/**
 * Команда строки размещаемого заказа.
 */
public class PlaceOrderLineCommand {

    /**
     * Идентификатор заказываемого товара.
     */
    private final ProductId productId;

    /**
     * Количество заказываемого товара.
     */
    private final Quantity quantity;

    /**
     * Цена единицы товара.
     */
    private final Money unitPrice;

    /**
     * Создает команду строки размещаемого заказа.
     *
     * @param productId идентификатор заказываемого товара
     * @param quantity количество заказываемого товара
     * @param unitPrice цена единицы товара
     */
    public PlaceOrderLineCommand(ProductId productId, Quantity quantity, Money unitPrice) {
        this.productId = Objects.requireNonNull(productId, "Идентификатор товара не может быть null");
        this.quantity = Objects.requireNonNull(quantity, "Количество товара не может быть null");
        this.unitPrice = Objects.requireNonNull(unitPrice, "Цена единицы товара не может быть null");
    }

    /**
     * Возвращает идентификатор заказываемого товара.
     *
     * @return идентификатор товара
     */
    public ProductId productId() {
        return productId;
    }

    /**
     * Возвращает количество заказываемого товара.
     *
     * @return количество товара
     */
    public Quantity quantity() {
        return quantity;
    }

    /**
     * Возвращает цену единицы товара.
     *
     * @return цена единицы товара
     */
    public Money unitPrice() {
        return unitPrice;
    }
}
