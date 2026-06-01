package dev.tschumin.ordermanagement.order.domain.valueobject;

import java.math.BigDecimal;
import java.util.Currency;

/**
 * Денежная сумма.
 */
public class Money {

    /**
     * Числовое значение денежной суммы.
     */
    private BigDecimal amount;

    /**
     * Валюта денежной суммы.
     */
    private Currency currency;
}
