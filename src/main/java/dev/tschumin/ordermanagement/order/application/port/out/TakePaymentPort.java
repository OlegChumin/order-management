package dev.tschumin.ordermanagement.order.application.port.out;

import dev.tschumin.ordermanagement.order.domain.valueobject.Money;
import dev.tschumin.ordermanagement.order.domain.valueobject.OrderId;

/**
 * Выходной порт списания оплаты.
 */
public interface TakePaymentPort {

    /**
     * Списывает оплату по заказу.
     *
     * @param orderId идентификатор заказа
     * @param amount сумма оплаты
     */
    void takePayment(OrderId orderId, Money amount);
}
