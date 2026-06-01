package dev.tschumin.ordermanagement.order.application.port.in;

import dev.tschumin.ordermanagement.order.application.command.CancelOrderCommand;

/**
 * Входной порт сценария отмены заказа.
 */
public interface CancelOrderUseCase {

    /**
     * Отменяет заказ.
     *
     * @param command команда отмены заказа
     */
    void cancelOrder(CancelOrderCommand command);
}
