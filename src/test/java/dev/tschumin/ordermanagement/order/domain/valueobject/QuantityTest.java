package dev.tschumin.ordermanagement.order.domain.valueobject;

import dev.tschumin.ordermanagement.order.domain.exception.OrderDomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Тесты количества товара.
 *
 * <ul>
 *     <li>Проверяет успешное создание положительного количества.</li>
 *     <li>Проверяет запрет нулевого количества.</li>
 *     <li>Проверяет запрет отрицательного количества.</li>
 * </ul>
 */
class QuantityTest {

    @Test
    @DisplayName("Должен успешно создавать положительное количество")
    void shouldCreatePositiveQuantity() {
        Quantity quantity = new Quantity(3);

        assertThat(quantity.value()).isEqualTo(3);
    }

    @Test
    @DisplayName("Должен запрещать нулевое количество")
    void shouldRejectZeroQuantity() {
        assertThatThrownBy(() -> new Quantity(0))
                .isInstanceOf(OrderDomainException.class)
                .hasMessage("Количество товара должно быть больше нуля");
    }

    @Test
    @DisplayName("Должен запрещать отрицательное количество")
    void shouldRejectNegativeQuantity() {
        assertThatThrownBy(() -> new Quantity(-1))
                .isInstanceOf(OrderDomainException.class)
                .hasMessage("Количество товара должно быть больше нуля");
    }
}
