package dev.tschumin.ordermanagement.order.application.port.out;

import dev.tschumin.ordermanagement.order.domain.model.Order;

/**
 * Выходной порт взаимодействия со складскими остатками.
 */
public interface InventoryPort {

    /**
     * Резервирует складские остатки для заказа.
     *
     * @param order заказ, по которому требуется резервирование
     */
    void reserve(Order order);
}
