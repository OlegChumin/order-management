package dev.tschumin.ordermanagement.order.domain.valueobject;

import java.util.Objects;
import java.util.UUID;

/**
 * Идентификатор товара.
 *
 * @param value значение идентификатора товара
 */
public record ProductId(UUID value) {

    /**
     * Создает идентификатор товара.
     *
     * @param value значение идентификатора товара
     */
    public ProductId {
        Objects.requireNonNull(value, "Идентификатор товара не может быть null");
    }
}
