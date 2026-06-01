package dev.tschumin.ordermanagement.order.application.port.out;

import dev.tschumin.ordermanagement.order.domain.model.Order;

/**
 * Выходной порт сохранения заказа.
 */
public interface SaveOrderPort {

    /**
     * Сохраняет заказ.
     *
     * @param order заказ для сохранения
     */
    void save(Order order);
}
