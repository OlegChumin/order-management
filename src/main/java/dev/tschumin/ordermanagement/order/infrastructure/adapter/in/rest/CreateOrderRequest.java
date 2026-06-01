package dev.tschumin.ordermanagement.order.infrastructure.adapter.in.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

/**
 * REST-запрос на создание заказа.
 */
public class CreateOrderRequest {

    /**
     * Идентификатор клиента.
     */
    @NotNull(message = "Идентификатор клиента обязателен")
    private UUID customerId;

    /**
     * Строки создаваемого заказа.
     */
    @Valid
    @NotEmpty(message = "Заказ должен содержать хотя бы одну строку")
    private List<CreateOrderLineRequest> lines;

    /**
     * Создает пустой REST-запрос на создание заказа.
     */
    public CreateOrderRequest() {
    }

    /**
     * Создает REST-запрос на создание заказа.
     *
     * @param customerId идентификатор клиента
     * @param lines строки создаваемого заказа
     */
    public CreateOrderRequest(UUID customerId, List<CreateOrderLineRequest> lines) {
        this.customerId = customerId;
        this.lines = lines;
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
     * Устанавливает идентификатор клиента.
     *
     * @param customerId идентификатор клиента
     */
    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    /**
     * Возвращает строки создаваемого заказа.
     *
     * @return строки создаваемого заказа
     */
    public List<CreateOrderLineRequest> getLines() {
        return lines;
    }

    /**
     * Устанавливает строки создаваемого заказа.
     *
     * @param lines строки создаваемого заказа
     */
    public void setLines(List<CreateOrderLineRequest> lines) {
        this.lines = lines;
    }
}
