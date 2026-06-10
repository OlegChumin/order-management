package dev.tschumin.ordermanagement.order.infrastructure.adapter.in.rest;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * REST-ответ со строкой заказа.
 */
public class OrderLineResponse {

    /**
     * Идентификатор товара.
     */
    private final UUID productId;

    /**
     * Количество товара.
     */
    private final int quantity;

    /**
     * Цена единицы товара.
     */
    private final BigDecimal unitPriceAmount;

    /**
     * Валюта цены единицы товара.
     */
    private final String unitPriceCurrency;

    /**
     * Итоговая сумма строки заказа.
     */
    private final BigDecimal totalAmount;

    /**
     * Валюта итоговой суммы строки заказа.
     */
    private final String totalCurrency;

    /**
     * Создает REST-ответ со строкой заказа.
     *
     * @param productId идентификатор товара
     * @param quantity количество товара
     * @param unitPriceAmount цена единицы товара
     * @param unitPriceCurrency валюта цены единицы товара
     * @param totalAmount итоговая сумма строки заказа
     * @param totalCurrency валюта итоговой суммы строки заказа
     */
    public OrderLineResponse(
            UUID productId,
            int quantity,
            BigDecimal unitPriceAmount,
            String unitPriceCurrency,
            BigDecimal totalAmount,
            String totalCurrency
    ) {
        this.productId = productId;
        this.quantity = quantity;
        this.unitPriceAmount = unitPriceAmount;
        this.unitPriceCurrency = unitPriceCurrency;
        this.totalAmount = totalAmount;
        this.totalCurrency = totalCurrency;
    }

    /**
     * Возвращает идентификатор товара.
     *
     * @return идентификатор товара
     */
    public UUID getProductId() {
        return productId;
    }

    /**
     * Возвращает количество товара.
     *
     * @return количество товара
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Возвращает цену единицы товара.
     *
     * @return цена единицы товара
     */
    public BigDecimal getUnitPriceAmount() {
        return unitPriceAmount;
    }

    /**
     * Возвращает валюту цены единицы товара.
     *
     * @return валюта цены единицы товара
     */
    public String getUnitPriceCurrency() {
        return unitPriceCurrency;
    }

    /**
     * Возвращает итоговую сумму строки заказа.
     *
     * @return итоговая сумма строки заказа
     */
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    /**
     * Возвращает валюту итоговой суммы строки заказа.
     *
     * @return валюта итоговой суммы строки заказа
     */
    public String getTotalCurrency() {
        return totalCurrency;
    }
}
