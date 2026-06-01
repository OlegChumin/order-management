package dev.tschumin.ordermanagement.order.domain.valueobject;

import dev.tschumin.ordermanagement.order.domain.exception.OrderDomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Currency;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Тесты денежной суммы.
 *
 * <ul>
 *     <li>Проверяет успешное создание положительной денежной суммы.</li>
 *     <li>Проверяет запрет нулевой, отрицательной и неполной денежной суммы.</li>
 *     <li>Проверяет сложение сумм в одной валюте.</li>
 *     <li>Проверяет запрет сложения сумм в разных валютах.</li>
 *     <li>Проверяет умножение суммы на количество.</li>
 * </ul>
 */
class MoneyTest {

    @Test
    @DisplayName("Должен успешно создавать положительную денежную сумму")
    void shouldCreatePositiveMoney() {
        Money money = money("12.34");

        assertThat(money.amount()).isEqualByComparingTo("12.34");
        assertThat(money.currency()).isEqualTo(eur());
    }

    @Test
    @DisplayName("Должен запрещать null в денежной сумме")
    void shouldRejectNullValues() {
        assertThatThrownBy(() -> new Money(null, eur()))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Денежная сумма не может быть null");
        assertThatThrownBy(() -> new Money(BigDecimal.ONE, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Валюта не может быть null");
    }

    @Test
    @DisplayName("Должен запрещать нулевую и отрицательную денежную сумму")
    void shouldRejectNonPositiveAmount() {
        assertThatThrownBy(() -> money("0.00"))
                .isInstanceOf(OrderDomainException.class)
                .hasMessage("Денежная сумма должна быть больше нуля");
        assertThatThrownBy(() -> money("-0.01"))
                .isInstanceOf(OrderDomainException.class)
                .hasMessage("Денежная сумма должна быть больше нуля");
    }

    @Test
    @DisplayName("Должен складывать денежные суммы в одной валюте")
    void shouldAddMoneyInSameCurrency() {
        Money result = money("10.00").add(money("2.50"));

        assertThat(result).isEqualTo(money("12.50"));
    }

    @Test
    @DisplayName("Должен запрещать сложение с null")
    void shouldRejectNullAddend() {
        assertThatThrownBy(() -> money("10.00").add(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Добавляемая денежная сумма не может быть null");
    }

    @Test
    @DisplayName("Должен запрещать сложение денежных сумм в разных валютах")
    void shouldRejectDifferentCurrenciesOnAdd() {
        Money eurMoney = money("10.00");
        Money usdMoney = new Money(new BigDecimal("1.00"), Currency.getInstance("USD"));

        assertThatThrownBy(() -> eurMoney.add(usdMoney))
                .isInstanceOf(OrderDomainException.class)
                .hasMessage("Нельзя складывать денежные суммы в разных валютах");
    }

    @Test
    @DisplayName("Должен умножать денежную сумму на количество")
    void shouldMultiplyMoneyByQuantity() {
        Money result = money("3.25").multiply(new Quantity(4));

        assertThat(result).isEqualTo(money("13.00"));
    }

    @Test
    @DisplayName("Должен запрещать умножение на null")
    void shouldRejectNullQuantityOnMultiply() {
        assertThatThrownBy(() -> money("3.25").multiply(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Количество товара не может быть null");
    }

    /**
     * Создает тестовую денежную сумму в евро.
     *
     * @param amount числовое значение суммы
     * @return денежная сумма
     */
    private static Money money(String amount) {
        return new Money(new BigDecimal(amount), eur());
    }

    /**
     * Возвращает валюту евро для тестов.
     *
     * @return валюта евро
     */
    private static Currency eur() {
        return Currency.getInstance("EUR");
    }
}
