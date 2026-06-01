package dev.tschumin.ordermanagement.order.application.usecase;

import dev.tschumin.ordermanagement.order.application.command.CancelOrderCommand;
import dev.tschumin.ordermanagement.order.application.exception.OrderNotFoundException;
import dev.tschumin.ordermanagement.order.application.port.in.CancelOrderUseCase;
import dev.tschumin.ordermanagement.order.application.port.out.LoadOrderPort;
import dev.tschumin.ordermanagement.order.application.port.out.SaveOrderPort;
import dev.tschumin.ordermanagement.order.domain.model.Order;

import java.util.Objects;

/**
 * Обработчик сценария отмены заказа.
 */
public class CancelOrderHandler implements CancelOrderUseCase {

    /**
     * Порт загрузки заказа.
     */
    private final LoadOrderPort loadOrderPort;

    /**
     * Порт сохранения заказа.
     */
    private final SaveOrderPort saveOrderPort;

    /**
     * Создает обработчик сценария отмены заказа.
     *
     * @param loadOrderPort порт загрузки заказа
     * @param saveOrderPort порт сохранения заказа
     */
    public CancelOrderHandler(LoadOrderPort loadOrderPort, SaveOrderPort saveOrderPort) {
        this.loadOrderPort = Objects.requireNonNull(loadOrderPort, "Порт загрузки заказа не может быть null");
        this.saveOrderPort = Objects.requireNonNull(saveOrderPort, "Порт сохранения заказа не может быть null");
    }

    /**
     * Отменяет заказ через доменную модель и сохраняет измененное состояние.
     *
     * @param command команда отмены заказа
     */
    @Override
    public void cancelOrder(CancelOrderCommand command) {
        Objects.requireNonNull(command, "Команда отмены заказа не может быть null");

        Order order = loadOrderPort.load(command.orderId())
                .orElseThrow(() -> new OrderNotFoundException(command.orderId()));
        order.cancel();
        saveOrderPort.save(order);
    }
}
