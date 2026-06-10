package dev.tschumin.ordermanagement.order.domain.valueobject;

/**
 * Статус заказа.
 */
public enum OrderStatus {

    /**
     * Заказ размещен и ожидает резервирования товаров.
     */
    PLACED,

    /**
     * Товары по заказу зарезервированы.
     */
    INVENTORY_RESERVED,

    /**
     * Оплата заказа начата.
     */
    PAYMENT_STARTED,

    /**
     * Заказ оплачен.
     */
    PAID,

    /**
     * Оплата заказа завершилась ошибкой.
     */
    PAYMENT_FAILED,

    /**
     * Заказ отменен.
     */
    CANCELLED
}
