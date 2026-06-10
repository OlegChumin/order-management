package dev.tschumin.ordermanagement.order.domain.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тесты статусов заказа.
 *
 * <ul>
 *     <li>Проверяет полный набор статусов заказа.</li>
 *     <li>Проверяет имена статусов, используемые в переходах агрегата.</li>
 * </ul>
 */
class OrderStatusTest {

    @Test
    @DisplayName("Должен содержать все доменные статусы заказа")
    void shouldContainAllOrderStatuses() {
        assertThat(OrderStatus.values()).containsExactly(
                OrderStatus.PLACED,
                OrderStatus.INVENTORY_RESERVED,
                OrderStatus.PAYMENT_STARTED,
                OrderStatus.PAID,
                OrderStatus.PAYMENT_FAILED,
                OrderStatus.CANCELLED
        );
    }

    @Test
    @DisplayName("Должен возвращать ожидаемые имена статусов")
    void shouldReturnExpectedStatusNames() {
        assertThat(OrderStatus.PLACED.name()).isEqualTo("PLACED");
        assertThat(OrderStatus.INVENTORY_RESERVED.name()).isEqualTo("INVENTORY_RESERVED");
        assertThat(OrderStatus.PAYMENT_STARTED.name()).isEqualTo("PAYMENT_STARTED");
        assertThat(OrderStatus.PAID.name()).isEqualTo("PAID");
        assertThat(OrderStatus.PAYMENT_FAILED.name()).isEqualTo("PAYMENT_FAILED");
        assertThat(OrderStatus.CANCELLED.name()).isEqualTo("CANCELLED");
    }
}
