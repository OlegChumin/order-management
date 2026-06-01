package dev.tschumin.ordermanagement.order.application.usecase;

import dev.tschumin.ordermanagement.order.application.exception.OrderNotFoundException;
import dev.tschumin.ordermanagement.order.application.port.in.GetOrderUseCase;
import dev.tschumin.ordermanagement.order.application.port.out.LoadOrderPort;
import dev.tschumin.ordermanagement.order.domain.model.Order;
import dev.tschumin.ordermanagement.order.domain.valueobject.OrderId;

import java.util.Objects;

/**
 * Обработчик сценария получения заказа.
 */
public class GetOrderHandler implements GetOrderUseCase {

    /**
     * Порт загрузки заказа.
     */
    private final LoadOrderPort loadOrderPort;

    /**
     * Создает обработчик сценария получения заказа.
     *
     * @param loadOrderPort порт загрузки заказа
     */
    public GetOrderHandler(LoadOrderPort loadOrderPort) {
        this.loadOrderPort = Objects.requireNonNull(loadOrderPort, "Порт загрузки заказа не может быть null");
    }

    /**
     * Возвращает заказ по идентификатору.
     *
     * @param orderId идентификатор заказа
     * @return найденный заказ
     */
    @Override
    public Order getOrder(OrderId orderId) {
        Objects.requireNonNull(orderId, "Идентификатор заказа не может быть null");
        return loadOrderPort.load(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }
}
