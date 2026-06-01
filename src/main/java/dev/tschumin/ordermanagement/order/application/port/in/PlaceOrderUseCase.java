package dev.tschumin.ordermanagement.order.application.port.in;

import dev.tschumin.ordermanagement.order.application.command.PlaceOrderCommand;
import dev.tschumin.ordermanagement.order.domain.model.Order;

/**
 * Входной порт сценария размещения заказа.
 */
public interface PlaceOrderUseCase {

    /**
     * Размещает новый заказ.
     *
     * @param command команда размещения заказа
     * @return размещенный заказ
     */
    Order placeOrder(PlaceOrderCommand command);
}
