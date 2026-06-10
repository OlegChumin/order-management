package dev.tschumin.ordermanagement.order.application.command;

import dev.tschumin.ordermanagement.order.domain.valueobject.CustomerId;

import java.util.List;
import java.util.Objects;

/**
 * Команда размещения заказа.
 */
public class PlaceOrderCommand {

    /**
     * Идентификатор клиента, размещающего заказ.
     */
    private final CustomerId customerId;

    /**
     * Строки размещаемого заказа.
     */
    private final List<PlaceOrderLineCommand> lines;

    /**
     * Создает команду размещения заказа.
     *
     * @param customerId идентификатор клиента, размещающего заказ
     * @param lines строки размещаемого заказа
     */
    public PlaceOrderCommand(CustomerId customerId, List<PlaceOrderLineCommand> lines) {
        this.customerId = Objects.requireNonNull(customerId, "Идентификатор клиента не может быть null");
        this.lines = List.copyOf(Objects.requireNonNull(lines, "Строки заказа не могут быть null"));
    }

    /**
     * Возвращает идентификатор клиента, размещающего заказ.
     *
     * @return идентификатор клиента
     */
    public CustomerId customerId() {
        return customerId;
    }

    /**
     * Возвращает строки размещаемого заказа.
     *
     * @return неизменяемый список строк команды
     */
    public List<PlaceOrderLineCommand> lines() {
        return lines;
    }
}
