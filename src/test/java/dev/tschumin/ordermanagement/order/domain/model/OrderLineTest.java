package dev.tschumin.ordermanagement.order.domain.model;

import dev.tschumin.ordermanagement.order.domain.valueobject.Money;
import dev.tschumin.ordermanagement.order.domain.valueobject.ProductId;
import dev.tschumin.ordermanagement.order.domain.valueobject.Quantity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Тесты строки заказа.
 *
 * <ul>
 *     <li>Проверяет успешное создание строки заказа.</li>
 *     <li>Проверяет расчет стоимости строки заказа.</li>
 *     <li>Проверяет обязательность товара, количества и цены.</li>
 * </ul>
 */
class OrderLineTest {

    @Test
    @DisplayName("Должен успешно создавать строку заказа")
    void shouldCreateOrderLineSuccessfully() {
        ProductId productId = productId();
        Quantity quantity = new Quantity(2);
        Money unitPrice = money("7.25");

        OrderLine line = new OrderLine(productId, quantity, unitPrice);

        assertThat(line.productId()).isEqualTo(productId);
        assertThat(line.quantity()).isEqualTo(quantity);
        assertThat(line.unitPrice()).isEqualTo(unitPrice);
    }

    @Test
    @DisplayName("Должен вычислять стоимость строки заказа")
    void shouldCalculateLineTotalPrice() {
        OrderLine line = new OrderLine(productId(), new Quantity(4), money("3.50"));

        assertThat(line.totalPrice()).isEqualTo(money("14.00"));
    }

    @Test
    @DisplayName("Должен запрещать null в строке заказа")
    void shouldRejectNullValues() {
        ProductId productId = productId();
        Quantity quantity = new Quantity(1);
        Money unitPrice = money("1.00");

        assertThatThrownBy(() -> new OrderLine(null, quantity, unitPrice))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Идентификатор товара не может быть null");
        assertThatThrownBy(() -> new OrderLine(productId, null, unitPrice))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Количество товара не может быть null");
        assertThatThrownBy(() -> new OrderLine(productId, quantity, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Цена единицы товара не может быть null");
        assertThatThrownBy(() -> new ProductId(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Идентификатор товара не может быть null");
    }

    /**
     * Создает тестовый идентификатор товара.
     *
     * @return идентификатор товара
     */
    private static ProductId productId() {
        return new ProductId(UUID.randomUUID());
    }

    /**
     * Создает тестовую денежную сумму в евро.
     *
     * @param amount числовое значение суммы
     * @return денежная сумма
     */
    private static Money money(String amount) {
        return new Money(new BigDecimal(amount), Currency.getInstance("EUR"));
    }
}
