package dev.tschumin.ordermanagement.order.infrastructure.adapter.out.payment;

import dev.tschumin.ordermanagement.order.application.port.out.PaymentPort;
import dev.tschumin.ordermanagement.order.domain.valueobject.Money;
import dev.tschumin.ordermanagement.order.domain.valueobject.OrderId;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Адаптер взаимодействия с платежной системой.
 */
@Component
public class PaymentAdapter implements PaymentPort {

    /**
     * Запускает оплату заказа.
     *
     * @param orderId идентификатор заказа
     * @param amount сумма оплаты
     */
    @Override
    public void startPayment(OrderId orderId, Money amount) {
        Objects.requireNonNull(orderId, "Идентификатор заказа для оплаты не может быть null");
        Objects.requireNonNull(amount, "Сумма оплаты не может быть null");
    }
}
