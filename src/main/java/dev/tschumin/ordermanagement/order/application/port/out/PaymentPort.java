package dev.tschumin.ordermanagement.order.application.port.out;

import dev.tschumin.ordermanagement.order.domain.valueobject.Money;
import dev.tschumin.ordermanagement.order.domain.valueobject.OrderId;

/**
 * Выходной порт взаимодействия с платежной системой.
 */
public interface PaymentPort {

    /**
     * Запускает оплату заказа.
     *
     * @param orderId идентификатор заказа
     * @param amount сумма оплаты
     */
    void startPayment(OrderId orderId, Money amount);
}
