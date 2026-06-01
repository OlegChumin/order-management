package dev.tschumin.ordermanagement.order.application.port.in;

import dev.tschumin.ordermanagement.order.application.dto.OrderDto;
import dev.tschumin.ordermanagement.order.domain.valueobject.OrderId;

/**
 * Входной порт сценария получения заказа.
 */
public interface GetOrderUseCase {

    /**
     * Возвращает заказ по идентификатору.
     *
     * @param orderId идентификатор заказа
     * @return представление заказа
     */
    OrderDto getOrder(OrderId orderId);
}
