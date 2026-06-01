package dev.tschumin.ordermanagement.order.infrastructure.adapter.in.rest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * REST-запрос на создание строки заказа.
 */
public class CreateOrderLineRequest {

    /**
     * Идентификатор товара.
     */
    @NotNull(message = "Идентификатор товара обязателен")
    private UUID productId;

    /**
     * Количество товара.
     */
    @Positive(message = "Количество товара должно быть больше нуля")
    private int quantity;

    /**
     * Цена единицы товара.
     */
    @NotNull(message = "Цена единицы товара обязательна")
    @Positive(message = "Цена единицы товара должна быть больше нуля")
    private BigDecimal unitPriceAmount;

    /**
     * Валюта цены единицы товара.
     */
    @NotBlank(message = "Валюта цены обязательна")
    @Size(min = 3, max = 3, message = "Код валюты должен содержать 3 символа")
    private String unitPriceCurrency;

    /**
     * Создает пустой REST-запрос на создание строки заказа.
     */
    public CreateOrderLineRequest() {
    }

    /**
     * Создает REST-запрос на создание строки заказа.
     *
     * @param productId идентификатор товара
     * @param quantity количество товара
     * @param unitPriceAmount цена единицы товара
     * @param unitPriceCurrency валюта цены единицы товара
     */
    public CreateOrderLineRequest(UUID productId, int quantity, BigDecimal unitPriceAmount, String unitPriceCurrency) {
        this.productId = productId;
        this.quantity = quantity;
        this.unitPriceAmount = unitPriceAmount;
        this.unitPriceCurrency = unitPriceCurrency;
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
     * Устанавливает идентификатор товара.
     *
     * @param productId идентификатор товара
     */
    public void setProductId(UUID productId) {
        this.productId = productId;
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
     * Устанавливает количество товара.
     *
     * @param quantity количество товара
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
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
     * Устанавливает цену единицы товара.
     *
     * @param unitPriceAmount цена единицы товара
     */
    public void setUnitPriceAmount(BigDecimal unitPriceAmount) {
        this.unitPriceAmount = unitPriceAmount;
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
     * Устанавливает валюту цены единицы товара.
     *
     * @param unitPriceCurrency валюта цены единицы товара
     */
    public void setUnitPriceCurrency(String unitPriceCurrency) {
        this.unitPriceCurrency = unitPriceCurrency;
    }
}
