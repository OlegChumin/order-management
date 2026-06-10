package dev.tschumin.ordermanagement.order.infrastructure.adapter.out.inventory;

import dev.tschumin.ordermanagement.order.domain.model.Order;
import dev.tschumin.ordermanagement.order.domain.model.OrderLine;
import dev.tschumin.ordermanagement.order.domain.valueobject.CustomerId;
import dev.tschumin.ordermanagement.order.domain.valueobject.Money;
import dev.tschumin.ordermanagement.order.domain.valueobject.OrderId;
import dev.tschumin.ordermanagement.order.domain.valueobject.ProductId;
import dev.tschumin.ordermanagement.order.domain.valueobject.Quantity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Тесты fake-адаптера складской системы.
 *
 * <ul>
 *     <li>Проверяет успешное резервирование заказа без внешней интеграции.</li>
 *     <li>Проверяет ошибку при отсутствии заказа.</li>
 * </ul>
 */
class FakeInventoryAdapterTest {

    @Test
    @DisplayName("Должен принять заказ для резервирования")
    void shouldAcceptOrderForReservation() {
        FakeInventoryAdapter adapter = new FakeInventoryAdapter();

        assertThatCode(() -> adapter.reserve(order())).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Должен отклонить null вместо заказа")
    void shouldRejectNullOrder() {
        FakeInventoryAdapter adapter = new FakeInventoryAdapter();

        assertThatThrownBy(() -> adapter.reserve(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Заказ для резервирования не может быть null");
    }

    /**
     * Создает тестовый заказ.
     *
     * @return тестовый заказ
     */
    private Order order() {
        return Order.create(
                OrderId.newId(),
                new CustomerId(UUID.fromString("00000000-0000-0000-0000-000000000001")),
                List.of(new OrderLine(
                        new ProductId(UUID.fromString("00000000-0000-0000-0000-000000000002")),
                        new Quantity(1),
                        new Money(new BigDecimal("10.00"), Currency.getInstance("EUR"))
                ))
        );
    }
}
