package dev.tschumin.ordermanagement.order.application.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тесты DTO представления заказа.
 *
 * <ul>
 *     <li>Проверяет создание DTO представления заказа.</li>
 * </ul>
 */
class OrderDtoTest {

    @Test
    @DisplayName("Должен создавать DTO представления заказа")
    void shouldCreateOrderDto() {
        OrderDto orderDto = new OrderDto();

        assertThat(orderDto).isNotNull();
    }
}
