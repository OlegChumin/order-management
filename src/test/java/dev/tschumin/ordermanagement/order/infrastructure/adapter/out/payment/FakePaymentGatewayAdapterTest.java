package dev.tschumin.ordermanagement.order.infrastructure.adapter.out.payment;

import dev.tschumin.ordermanagement.order.domain.valueobject.Money;
import dev.tschumin.ordermanagement.order.domain.valueobject.OrderId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Currency;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Тесты fake-адаптера платежного шлюза.
 *
 * <ul>
 *     <li>Проверяет успешный запуск оплаты без внешней интеграции.</li>
 *     <li>Проверяет ошибки при отсутствии идентификатора заказа или суммы оплаты.</li>
 * </ul>
 */
class FakePaymentGatewayAdapterTest {

    @Test
    @DisplayName("Должен принять запуск оплаты")
    void shouldAcceptPaymentStart() {
        FakePaymentGatewayAdapter adapter = new FakePaymentGatewayAdapter();

        assertThatCode(() -> adapter.startPayment(OrderId.newId(), money()))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Должен отклонить null вместо идентификатора заказа")
    void shouldRejectNullOrderId() {
        FakePaymentGatewayAdapter adapter = new FakePaymentGatewayAdapter();

        assertThatThrownBy(() -> adapter.startPayment(null, money()))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Идентификатор заказа для оплаты не может быть null");
    }

    @Test
    @DisplayName("Должен отклонить null вместо суммы оплаты")
    void shouldRejectNullAmount() {
        FakePaymentGatewayAdapter adapter = new FakePaymentGatewayAdapter();

        assertThatThrownBy(() -> adapter.startPayment(OrderId.newId(), null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Сумма оплаты не может быть null");
    }

    /**
     * Создает тестовую денежную сумму.
     *
     * @return денежная сумма
     */
    private Money money() {
        return new Money(new BigDecimal("10.00"), Currency.getInstance("EUR"));
    }
}
