package dev.tschumin.ordermanagement.order.application.usecase;

import dev.tschumin.ordermanagement.order.application.command.PlaceOrderCommand;
import dev.tschumin.ordermanagement.order.application.command.PlaceOrderLineCommand;
import dev.tschumin.ordermanagement.order.application.port.in.PlaceOrderUseCase;
import dev.tschumin.ordermanagement.order.application.port.out.DomainEventPublisherPort;
import dev.tschumin.ordermanagement.order.application.port.out.InventoryPort;
import dev.tschumin.ordermanagement.order.application.port.out.PaymentPort;
import dev.tschumin.ordermanagement.order.application.port.out.SaveOrderPort;
import dev.tschumin.ordermanagement.order.domain.event.DomainEvent;
import dev.tschumin.ordermanagement.order.domain.model.Order;
import dev.tschumin.ordermanagement.order.domain.model.OrderLine;
import dev.tschumin.ordermanagement.order.domain.valueobject.OrderId;

import java.util.List;
import java.util.Objects;

/**
 * Обработчик сценария размещения заказа.
 */
public class PlaceOrderHandler implements PlaceOrderUseCase {

    /**
     * Порт сохранения заказа.
     */
    private final SaveOrderPort saveOrderPort;

    /**
     * Порт взаимодействия со складскими остатками.
     */
    private final InventoryPort inventoryPort;

    /**
     * Порт взаимодействия с платежной системой.
     */
    private final PaymentPort paymentPort;

    /**
     * Порт публикации доменных событий.
     */
    private final DomainEventPublisherPort domainEventPublisherPort;

    /**
     * Создает обработчик сценария размещения заказа.
     *
     * @param saveOrderPort порт сохранения заказа
     * @param inventoryPort порт взаимодействия со складскими остатками
     * @param paymentPort порт взаимодействия с платежной системой
     * @param domainEventPublisherPort порт публикации доменных событий
     */
    public PlaceOrderHandler(
            SaveOrderPort saveOrderPort,
            InventoryPort inventoryPort,
            PaymentPort paymentPort,
            DomainEventPublisherPort domainEventPublisherPort
    ) {
        this.saveOrderPort = Objects.requireNonNull(saveOrderPort, "Порт сохранения заказа не может быть null");
        this.inventoryPort = Objects.requireNonNull(inventoryPort, "Порт складских остатков не может быть null");
        this.paymentPort = Objects.requireNonNull(paymentPort, "Порт оплаты не может быть null");
        this.domainEventPublisherPort = Objects.requireNonNull(
                domainEventPublisherPort,
                "Порт публикации доменных событий не может быть null"
        );
    }

    /**
     * Размещает заказ, резервирует остатки, запускает оплату и публикует доменные события.
     *
     * @param command команда размещения заказа
     * @return размещенный заказ
     */
    @Override
    public Order placeOrder(PlaceOrderCommand command) {
        Objects.requireNonNull(command, "Команда размещения заказа не может быть null");

        Order order = Order.create(OrderId.newId(), command.customerId(), toOrderLines(command.lines()));
        inventoryPort.reserve(order);
        order.reserveInventory();
        paymentPort.startPayment(order.id(), order.totalPrice());
        order.markPaymentStarted();
        saveOrderPort.save(order);
        publishDomainEvents(order);

        return order;
    }

    /**
     * Преобразует строки команды в доменные строки заказа.
     *
     * @param lines строки команды размещения заказа
     * @return доменные строки заказа
     */
    private List<OrderLine> toOrderLines(List<PlaceOrderLineCommand> lines) {
        return lines.stream()
                .map(line -> new OrderLine(line.productId(), line.quantity(), line.unitPrice()))
                .toList();
    }

    /**
     * Публикует накопленные доменные события заказа и очищает их в агрегате.
     *
     * @param order заказ с накопленными доменными событиями
     */
    private void publishDomainEvents(Order order) {
        for (DomainEvent event : order.domainEvents()) {
            domainEventPublisherPort.publish(event);
        }
        order.clearDomainEvents();
    }
}
