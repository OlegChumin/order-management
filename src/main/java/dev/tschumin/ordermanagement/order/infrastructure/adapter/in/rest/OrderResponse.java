package dev.tschumin.ordermanagement.order.infrastructure.adapter.in.rest;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * REST-ответ с заказом.
 */
public class OrderResponse {

    /**
     * Идентификатор заказа.
     */
    private final UUID id;

    /**
     * Идентификатор клиента.
     */
    private final UUID customerId;

    /**
     * Статус заказа.
     */
    private final String status;

    /**
     * Признак резервирования складских остатков.
     */
    private final boolean inventoryReserved;

    /**
     * Строки заказа.
     */
    private final List<OrderLineResponse> lines;

    /**
     * Итоговая сумма заказа.
     */
    private final BigDecimal totalAmount;

    /**
     * Валюта итоговой суммы заказа.
     */
    private final String totalCurrency;

    /**
     * Создает REST-ответ с заказом.
     *
     * @param id идентификатор заказа
     * @param customerId идентификатор клиента
     * @param status статус заказа
     * @param inventoryReserved признак резервирования складских остатков
     * @param lines строки заказа
     * @param totalAmount итоговая сумма заказа
     * @param totalCurrency валюта итоговой суммы заказа
     */
    public OrderResponse(
            UUID id,
            UUID customerId,
            String status,
            boolean inventoryReserved,
            List<OrderLineResponse> lines,
            BigDecimal totalAmount,
            String totalCurrency
    ) {
        this.id = id;
        this.customerId = customerId;
        this.status = status;
        this.inventoryReserved = inventoryReserved;
        this.lines = List.copyOf(lines);
        this.totalAmount = totalAmount;
        this.totalCurrency = totalCurrency;
    }

    /**
     * Возвращает идентификатор заказа.
     *
     * @return идентификатор заказа
     */
    public UUID getId() {
        return id;
    }

    /**
     * Возвращает идентификатор клиента.
     *
     * @return идентификатор клиента
     */
    public UUID getCustomerId() {
        return customerId;
    }

    /**
     * Возвращает статус заказа.
     *
     * @return статус заказа
     */
    public String getStatus() {
        return status;
    }

    /**
     * Возвращает признак резервирования складских остатков.
     *
     * @return {@code true}, если остатки зарезервированы
     */
    public boolean isInventoryReserved() {
        return inventoryReserved;
    }

    /**
     * Возвращает строки заказа.
     *
     * @return строки заказа
     */
    public List<OrderLineResponse> getLines() {
        return lines;
    }

    /**
     * Возвращает итоговую сумму заказа.
     *
     * @return итоговая сумма заказа
     */
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    /**
     * Возвращает валюту итоговой суммы заказа.
     *
     * @return валюта итоговой суммы заказа
     */
    public String getTotalCurrency() {
        return totalCurrency;
    }
}
