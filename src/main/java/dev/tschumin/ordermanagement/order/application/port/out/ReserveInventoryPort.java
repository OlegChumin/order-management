package dev.tschumin.ordermanagement.order.application.port.out;

import dev.tschumin.ordermanagement.order.domain.valueobject.ProductId;
import dev.tschumin.ordermanagement.order.domain.valueobject.Quantity;

/**
 * Выходной порт резервирования складских остатков.
 */
public interface ReserveInventoryPort {

    /**
     * Резервирует количество товара.
     *
     * @param productId идентификатор товара
     * @param quantity количество товара
     */
    void reserve(ProductId productId, Quantity quantity);
}
