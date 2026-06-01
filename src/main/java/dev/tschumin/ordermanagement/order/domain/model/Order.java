package dev.tschumin.ordermanagement.order.domain.model;

import dev.tschumin.ordermanagement.order.domain.valueobject.CustomerId;
import dev.tschumin.ordermanagement.order.domain.valueobject.Money;
import dev.tschumin.ordermanagement.order.domain.valueobject.OrderId;
import dev.tschumin.ordermanagement.order.domain.valueobject.OrderStatus;

import java.util.List;

/**
 * Агрегат заказа в доменной модели.
 */
public class Order {

    /**
     * Идентификатор заказа.
     */
    private OrderId id;

    /**
     * Идентификатор клиента, оформившего заказ.
     */
    private CustomerId customerId;

    /**
     * Строки заказа с выбранными товарами.
     */
    private List<OrderLine> lines;

    /**
     * Текущий статус заказа.
     */
    private OrderStatus status;

    /**
     * Итоговая стоимость заказа.
     */
    private Money totalAmount;
}
