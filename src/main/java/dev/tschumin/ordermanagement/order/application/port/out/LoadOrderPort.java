package dev.tschumin.ordermanagement.order.application.port.out;

import dev.tschumin.ordermanagement.order.domain.model.Order;
import dev.tschumin.ordermanagement.order.domain.valueobject.OrderId;

import java.util.Optional;

/**
 * Выходной порт загрузки заказа.
 */
public interface LoadOrderPort {

    /**
     * Загружает заказ по идентификатору.
     *
     * @param orderId идентификатор заказа
     * @return найденный заказ
     */
    Optional<Order> load(OrderId orderId);
}
