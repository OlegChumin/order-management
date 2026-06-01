package dev.tschumin.ordermanagement.order.application.port.in;

import dev.tschumin.ordermanagement.order.domain.model.Order;
import dev.tschumin.ordermanagement.order.domain.valueobject.OrderId;

/**
 * Входной порт сценария получения заказа.
 */
public interface GetOrderUseCase {

    /**
     * Возвращает заказ по идентификатору.
     *
     * @param orderId идентификатор заказа
     * @return найденный заказ
     */
    Order getOrder(OrderId orderId);
}
