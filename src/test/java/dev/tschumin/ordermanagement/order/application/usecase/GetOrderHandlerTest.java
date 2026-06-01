package dev.tschumin.ordermanagement.order.application.usecase;

import dev.tschumin.ordermanagement.order.application.exception.OrderNotFoundException;
import dev.tschumin.ordermanagement.order.application.port.out.LoadOrderPort;
import dev.tschumin.ordermanagement.order.domain.model.Order;
import dev.tschumin.ordermanagement.order.domain.model.OrderLine;
import dev.tschumin.ordermanagement.order.domain.valueobject.CustomerId;
import dev.tschumin.ordermanagement.order.domain.valueobject.Money;
import dev.tschumin.ordermanagement.order.domain.valueobject.OrderId;
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
import static org.mockito.Mockito.when;

/**
 * Тесты обработчика получения заказа.
 *
 * <ul>
 *     <li>Проверяет возврат найденного заказа.</li>
 *     <li>Проверяет ошибку при отсутствии заказа.</li>
 *     <li>Проверяет ошибку при отсутствии идентификатора.</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
class GetOrderHandlerTest {

    /**
     * Порт загрузки заказа.
     */
    @Mock
    private LoadOrderPort loadOrderPort;

    @Test
    @DisplayName("Должен вернуть найденный заказ")
    void shouldReturnFoundOrder() {
        OrderId orderId = OrderId.newId();
        Order order = order(orderId);
        when(loadOrderPort.load(orderId)).thenReturn(Optional.of(order));

        Order result = handler().getOrder(orderId);

        assertThat(result).isSameAs(order);
    }

    @Test
    @DisplayName("Должен выбросить ошибку, если заказ не найден")
    void shouldThrowWhenOrderNotFound() {
        OrderId orderId = OrderId.newId();
        when(loadOrderPort.load(orderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler().getOrder(orderId))
                .isInstanceOf(OrderNotFoundException.class)
                .hasMessage("Заказ не найден: " + orderId.value());
    }

    @Test
    @DisplayName("Должен отклонить null вместо идентификатора заказа")
    void shouldRejectNullOrderId() {
        assertThatThrownBy(() -> handler().getOrder(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Идентификатор заказа не может быть null");
    }

    /**
     * Создает тестируемый обработчик получения заказа.
     *
     * @return обработчик получения заказа
     */
    private GetOrderHandler handler() {
        return new GetOrderHandler(loadOrderPort);
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
                        new Money(new BigDecimal("10.00"), Currency.getInstance("EUR"))
                ))
        );
    }
}
