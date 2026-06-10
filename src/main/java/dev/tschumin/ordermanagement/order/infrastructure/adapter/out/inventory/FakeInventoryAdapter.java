package dev.tschumin.ordermanagement.order.infrastructure.adapter.out.inventory;

import dev.tschumin.ordermanagement.order.application.port.out.InventoryPort;
import dev.tschumin.ordermanagement.order.domain.model.Order;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Fake-адаптер складской системы для локального сценария без внешней интеграции.
 */
@Component
public class FakeInventoryAdapter implements InventoryPort {

    /**
     * Резервирует складские остатки для заказа.
     *
     * @param order заказ, по которому требуется резервирование
     */
    @Override
    public void reserve(Order order) {
        Objects.requireNonNull(order, "Заказ для резервирования не может быть null");
    }
}
