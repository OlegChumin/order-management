package dev.tschumin.ordermanagement.order.domain.valueobject;

import dev.tschumin.ordermanagement.order.domain.exception.OrderDomainException;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;

/**
 * Денежная сумма.
 *
 * @param amount числовое значение денежной суммы
 * @param currency валюта денежной суммы
 */
public record Money(BigDecimal amount, Currency currency) {

    /**
     * Создает денежную сумму.
     *
     * @param amount числовое значение денежной суммы
     * @param currency валюта денежной суммы
     */
    public Money {
        Objects.requireNonNull(amount, "Денежная сумма не может быть null");
        Objects.requireNonNull(currency, "Валюта не может быть null");
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new OrderDomainException("Денежная сумма должна быть больше нуля");
        }
    }

    /**
     * Складывает две денежные суммы в одной валюте.
     *
     * @param other добавляемая денежная сумма
     * @return результат сложения
     */
    public Money add(Money other) {
        Objects.requireNonNull(other, "Добавляемая денежная сумма не может быть null");
        if (!currency.equals(other.currency())) {
            throw new OrderDomainException("Нельзя складывать денежные суммы в разных валютах");
        }
        return new Money(amount.add(other.amount()), currency);
    }

    /**
     * Умножает денежную сумму на количество.
     *
     * @param quantity количество товара
     * @return результат умножения
     */
    public Money multiply(Quantity quantity) {
        Objects.requireNonNull(quantity, "Количество товара не может быть null");
        return new Money(amount.multiply(BigDecimal.valueOf(quantity.value())), currency);
    }
}
