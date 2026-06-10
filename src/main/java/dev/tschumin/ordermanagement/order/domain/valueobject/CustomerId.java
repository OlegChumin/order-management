package dev.tschumin.ordermanagement.order.domain.valueobject;

import java.util.Objects;
import java.util.UUID;

/**
 * Идентификатор клиента.
 *
 * @param value значение идентификатора клиента
 */
public record CustomerId(UUID value) {

    /**
     * Создает идентификатор клиента.
     *
     * @param value значение идентификатора клиента
     */
    public CustomerId {
        Objects.requireNonNull(value, "Идентификатор клиента не может быть null");
    }
}
