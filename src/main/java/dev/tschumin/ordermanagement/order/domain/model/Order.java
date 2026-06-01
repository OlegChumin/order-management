package dev.tschumin.ordermanagement.order.domain.model;

import dev.tschumin.ordermanagement.order.domain.event.DomainEvent;
import dev.tschumin.ordermanagement.order.domain.event.OrderPlacedEvent;
import dev.tschumin.ordermanagement.order.domain.exception.OrderDomainException;
import dev.tschumin.ordermanagement.order.domain.valueobject.CustomerId;
import dev.tschumin.ordermanagement.order.domain.valueobject.Money;
import dev.tschumin.ordermanagement.order.domain.valueobject.OrderId;
import dev.tschumin.ordermanagement.order.domain.valueobject.OrderStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Корень агрегата заказа.
 *
 * <p>Заказ управляет строками заказа, текущим статусом, резервированием товаров,
 * оплатой и публикацией доменных событий. Все изменения состояния проходят через
 * методы агрегата, чтобы инварианты заказа оставались внутри доменной модели.</p>
 */
public class Order {

    /**
     * Идентификатор заказа.
     */
    private final OrderId id;

    /**
     * Идентификатор клиента, оформившего заказ.
     */
    private final CustomerId customerId;

    /**
     * Строки заказа с выбранными товарами.
     */
    private final List<OrderLine> lines;

    /**
     * Текущий статус заказа.
     */
    private OrderStatus status;

    /**
     * Признак того, что складские остатки уже были зарезервированы.
     */
    private boolean inventoryReserved;

    /**
     * Доменные события, накопленные агрегатом.
     */
    private final List<DomainEvent> domainEvents;

    /**
     * Создает заказ с начальным состоянием.
     *
     * @param id идентификатор заказа
     * @param customerId идентификатор клиента
     * @param lines строки заказа
     * @param status текущий статус заказа
     * @param inventoryReserved признак резервирования складских остатков
     */
    private Order(
            OrderId id,
            CustomerId customerId,
            List<OrderLine> lines,
            OrderStatus status,
            boolean inventoryReserved
    ) {
        this.id = Objects.requireNonNull(id, "Идентификатор заказа не может быть null");
        this.customerId = Objects.requireNonNull(customerId, "Идентификатор клиента не может быть null");
        this.lines = validateAndCopyLines(lines);
        this.status = Objects.requireNonNull(status, "Статус заказа не может быть null");
        this.inventoryReserved = inventoryReserved;
        this.domainEvents = new ArrayList<>();
    }

    /**
     * Создает новый заказ и регистрирует доменное событие размещения заказа.
     *
     * @param id идентификатор заказа
     * @param customerId идентификатор клиента
     * @param lines строки заказа
     * @return созданный заказ
     */
    public static Order create(OrderId id, CustomerId customerId, List<OrderLine> lines) {
        Order order = new Order(id, customerId, lines, OrderStatus.PLACED, false);
        order.registerEvent(new OrderPlacedEvent(order.id, Instant.now()));
        return order;
    }

    /**
     * Резервирует складские остатки по заказу.
     */
    public void reserveInventory() {
        if (inventoryReserved) {
            throw new OrderDomainException("Повторное резервирование заказа запрещено");
        }
        requireStatus(OrderStatus.PLACED, "Резервирование возможно только для размещенного заказа");
        inventoryReserved = true;
        status = OrderStatus.INVENTORY_RESERVED;
    }

    /**
     * Переводит заказ в состояние начатой оплаты.
     */
    public void markPaymentStarted() {
        requireStatus(OrderStatus.INVENTORY_RESERVED, "Оплату можно начать только после резервирования товаров");
        status = OrderStatus.PAYMENT_STARTED;
    }

    /**
     * Помечает заказ как оплаченный.
     */
    public void markPaid() {
        requireStatus(OrderStatus.PAYMENT_STARTED, "Оплаченным можно сделать только заказ с начатой оплатой");
        status = OrderStatus.PAID;
    }

    /**
     * Помечает оплату заказа как неуспешную.
     */
    public void failPayment() {
        requireStatus(OrderStatus.PAYMENT_STARTED, "Ошибку оплаты можно зафиксировать только для начатой оплаты");
        status = OrderStatus.PAYMENT_FAILED;
    }

    /**
     * Отменяет заказ, если он еще не был оплачен.
     */
    public void cancel() {
        if (status == OrderStatus.PAID) {
            throw new OrderDomainException("Отмена оплаченного заказа запрещена");
        }
        if (status == OrderStatus.CANCELLED) {
            throw new OrderDomainException("Заказ уже отменен");
        }
        status = OrderStatus.CANCELLED;
    }

    /**
     * Рассчитывает итоговую стоимость заказа по строкам заказа.
     *
     * @return итоговая стоимость заказа
     */
    public Money totalPrice() {
        Money total = lines.getFirst().totalPrice();
        for (int index = 1; index < lines.size(); index++) {
            total = total.add(lines.get(index).totalPrice());
        }
        return total;
    }

    /**
     * Возвращает идентификатор заказа.
     *
     * @return идентификатор заказа
     */
    public OrderId id() {
        return id;
    }

    /**
     * Возвращает идентификатор клиента.
     *
     * @return идентификатор клиента
     */
    public CustomerId customerId() {
        return customerId;
    }

    /**
     * Возвращает строки заказа.
     *
     * @return неизменяемый список строк заказа
     */
    public List<OrderLine> lines() {
        return lines;
    }

    /**
     * Возвращает текущий статус заказа.
     *
     * @return текущий статус заказа
     */
    public OrderStatus status() {
        return status;
    }

    /**
     * Возвращает признак резервирования складских остатков.
     *
     * @return {@code true}, если товары уже были зарезервированы
     */
    public boolean inventoryReserved() {
        return inventoryReserved;
    }

    /**
     * Возвращает накопленные доменные события.
     *
     * @return неизменяемый список доменных событий
     */
    public List<DomainEvent> domainEvents() {
        return List.copyOf(domainEvents);
    }

    /**
     * Очищает накопленные доменные события после их публикации.
     */
    public void clearDomainEvents() {
        domainEvents.clear();
    }

    /**
     * Регистрирует доменное событие внутри агрегата.
     *
     * @param event доменное событие
     */
    private void registerEvent(DomainEvent event) {
        domainEvents.add(Objects.requireNonNull(event, "Доменное событие не может быть null"));
    }

    /**
     * Проверяет, что заказ находится в ожидаемом статусе.
     *
     * @param expectedStatus ожидаемый статус заказа
     * @param message сообщение исключения при нарушении правила
     */
    private void requireStatus(OrderStatus expectedStatus, String message) {
        if (status != expectedStatus) {
            throw new OrderDomainException(message);
        }
    }

    /**
     * Проверяет строки заказа и создает их неизменяемую копию.
     *
     * @param lines строки заказа
     * @return неизменяемая копия строк заказа
     */
    private static List<OrderLine> validateAndCopyLines(List<OrderLine> lines) {
        Objects.requireNonNull(lines, "Строки заказа не могут быть null");
        if (lines.isEmpty()) {
            throw new OrderDomainException("Заказ не может быть пустым");
        }
        for (OrderLine line : lines) {
            Objects.requireNonNull(line, "Строка заказа не может быть null");
        }
        return List.copyOf(lines);
    }
}
