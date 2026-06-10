package dev.tschumin.ordermanagement.order.domain.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тесты доменного сервиса заказа.
 *
 * <ul>
 *     <li>Проверяет создание доменного сервиса заказа.</li>
 * </ul>
 */
class OrderDomainServiceTest {

    @Test
    @DisplayName("Должен создавать доменный сервис заказа")
    void shouldCreateOrderDomainService() {
        OrderDomainService service = new OrderDomainService();

        assertThat(service).isNotNull();
    }
}
