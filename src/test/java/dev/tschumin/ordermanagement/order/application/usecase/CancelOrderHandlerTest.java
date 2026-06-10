package dev.tschumin.ordermanagement.order.application.usecase;

import dev.tschumin.ordermanagement.order.application.command.CancelOrderCommand;
import dev.tschumin.ordermanagement.order.application.exception.OrderNotFoundException;
import dev.tschumin.ordermanagement.order.application.port.out.LoadOrderPort;
import dev.tschumin.ordermanagement.order.application.port.out.SaveOrderPort;
import dev.tschumin.ordermanagement.order.domain.model.Order;
import dev.tschumin.ordermanagement.order.domain.model.OrderLine;
import dev.tschumin.ordermanagement.order.domain.valueobject.CustomerId;
import dev.tschumin.ordermanagement.order.domain.valueobject.Money;
import dev.tschumin.ordermanagement.order.domain.valueobject.OrderId;
import dev.tschumin.ordermanagement.order.domain.valueobject.OrderStatus;
import dev.tschumin.ordermanagement.order.domain.valueobject.ProductId;
import dev.tschumin.ordermanagement.order.domain.valueobject.Quantity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Тесты обработчика отмены заказа.
 *
 * <ul>
 *     <li>Проверяет отмену найденного заказа через доменную модель.</li>
 *     <li>Проверяет ошибку при отсутствии заказа.</li>
 *     <li>Проверяет ошибку при отсутствии команды.</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
class CancelOrderHandlerTest {

    /**
     * Порт загрузки заказа.
     */
    @Mock
    private LoadOrderPort loadOrderPort;

    /**
     * Порт сохранения заказа.
     */
    @Mock
    private SaveOrderPort saveOrderPort;

    @Test
    @DisplayName("Должен отменить найденный заказ и сохранить его")
    void shouldCancelFoundOrder() {
        OrderId orderId = OrderId.newId();
        Order order = order(orderId);
        when(loadOrderPort.load(orderId)).thenReturn(Optional.of(order));

        handler().cancelOrder(new CancelOrderCommand(orderId));

        assertThat(order.status()).isEqualTo(OrderStatus.CANCELLED);
        verify(saveOrderPort).save(order);
    }

    @Test
    @DisplayName("Должен выбросить ошибку, если заказ не найден")
    void shouldThrowWhenOrderNotFound() {
        OrderId orderId = OrderId.newId();
        when(loadOrderPort.load(orderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler().cancelOrder(new CancelOrderCommand(orderId)))
                .isInstanceOf(OrderNotFoundException.class)
                .hasMessage("Заказ не найден: " + orderId.value());
        verifyNoInteractions(saveOrderPort);
    }

    @Test
    @DisplayName("Должен отклонить null вместо команды отмены")
    void shouldRejectNullCommand() {
        assertThatThrownBy(() -> handler().cancelOrder(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Команда отмены заказа не может быть null");
    }

    /**
     * Создает тестируемый обработчик отмены заказа.
     *
     * @return обработчик отмены заказа
     */
    private CancelOrderHandler handler() {
        return new CancelOrderHandler(loadOrderPort, saveOrderPort);
    }

    /**
     * Создает тестовый заказ.
     *
     * @param orderId идентификатор заказа
     * @return тестовый заказ
     */
    private Order order(OrderId orderId) {
        return Order.create(
                orderId,
                new CustomerId(UUID.fromString("00000000-0000-0000-0000-000000000001")),
                List.of(new OrderLine(
                        new ProductId(UUID.fromString("00000000-0000-0000-0000-000000000002")),
                        new Quantity(1),
                        money("10.00")
                ))
        );
    }

    /**
     * Создает денежную сумму в евро.
     *
     * @param amount числовое значение суммы
     * @return денежная сумма
     */
    private Money money(String amount) {
        return new Money(new BigDecimal(amount), Currency.getInstance("EUR"));
    }
}
