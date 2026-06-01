package dev.tschumin.ordermanagement.order.application.port.in;

import dev.tschumin.ordermanagement.order.application.command.PlaceOrderCommand;
import dev.tschumin.ordermanagement.order.application.dto.OrderDto;

/**
 * Входной порт сценария размещения заказа.
 */
public interface PlaceOrderUseCase {

    /**
     * Размещает новый заказ.
     *
     * @param command команда размещения заказа
     * @return представление размещенного заказа
     */
    OrderDto placeOrder(PlaceOrderCommand command);
}
