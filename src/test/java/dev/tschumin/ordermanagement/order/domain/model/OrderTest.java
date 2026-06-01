package dev.tschumin.ordermanagement.order.domain.model;

import dev.tschumin.ordermanagement.order.domain.event.DomainEvent;
import dev.tschumin.ordermanagement.order.domain.event.OrderPlacedEvent;
import dev.tschumin.ordermanagement.order.domain.exception.OrderDomainException;
import dev.tschumin.ordermanagement.order.domain.valueobject.CustomerId;
import dev.tschumin.ordermanagement.order.domain.valueobject.Money;
import dev.tschumin.ordermanagement.order.domain.valueobject.OrderId;
import dev.tschumin.ordermanagement.order.domain.valueobject.OrderStatus;
import dev.tschumin.ordermanagement.order.domain.valueobject.ProductId;
import dev.tschumin.ordermanagement.order.domain.valueobject.Quantity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Тесты корня агрегата заказа.
 *
 * <ul>
 *     <li>Проверяет успешное создание заказа и регистрацию доменного события.</li>
 *     <li>Проверяет расчет итоговой стоимости заказа.</li>
 *     <li>Проверяет допустимые переходы статусов заказа.</li>
 *     <li>Проверяет ошибки бизнес-валидации и запрещенные переходы.</li>
 *     <li>Проверяет защиту внутренних коллекций агрегата.</li>
 * </ul>
 */
class OrderTest {

    @Test
    @DisplayName("Должен успешно создавать заказ и регистрировать событие размещения")
    void shouldCreateOrderSuccessfully() {
        OrderId orderId = OrderId.newId();
        CustomerId customerId = customerId();
        List<OrderLine> lines = List.of(orderLine("10.00", 2));

        Order order = Order.create(orderId, customerId, lines);

        assertThat(order.id()).isEqualTo(orderId);
        assertThat(order.customerId()).isEqualTo(customerId);
        assertThat(order.lines()).containsExactlyElementsOf(lines);
        assertThat(order.status()).isEqualTo(OrderStatus.PLACED);
        assertThat(order.inventoryReserved()).isFalse();
        assertThat(order.domainEvents())
                .hasSize(1)
                .first()
                .isInstanceOf(OrderPlacedEvent.class);

        OrderPlacedEvent event = (OrderPlacedEvent) order.domainEvents().getFirst();
        assertThat(event.orderId()).isEqualTo(orderId);
        assertThat(event.occurredAt()).isNotNull();
    }

    @Test
    @DisplayName("Должен вычислять итоговую сумму заказа")
    void shouldCalculateTotalPrice() {
        Order order = Order.create(
                OrderId.newId(),
                customerId(),
                List.of(orderLine("10.00", 2), orderLine("5.50", 3))
        );

        assertThat(order.totalPrice()).isEqualTo(money("36.50"));
    }

    @Test
    @DisplayName("Должен восстанавливать заказ из сохраненного состояния без новых событий")
    void shouldRestoreOrderWithoutNewDomainEvents() {
        OrderId orderId = OrderId.newId();
        CustomerId customerId = customerId();
        List<OrderLine> lines = List.of(orderLine("10.00", 2));

        Order order = Order.restore(orderId, customerId, lines, OrderStatus.INVENTORY_RESERVED, true);

        assertThat(order.id()).isEqualTo(orderId);
        assertThat(order.customerId()).isEqualTo(customerId);
        assertThat(order.lines()).containsExactlyElementsOf(lines);
        assertThat(order.status()).isEqualTo(OrderStatus.INVENTORY_RESERVED);
        assertThat(order.inventoryReserved()).isTrue();
        assertThat(order.domainEvents()).isEmpty();
    }

    @Test
    @DisplayName("Должен проходить успешный сценарий резервирования и оплаты")
    void shouldMoveThroughSuccessfulPaymentFlow() {
        Order order = order();

        order.reserveInventory();
        assertThat(order.status()).isEqualTo(OrderStatus.INVENTORY_RESERVED);
        assertThat(order.inventoryReserved()).isTrue();

        order.markPaymentStarted();
        assertThat(order.status()).isEqualTo(OrderStatus.PAYMENT_STARTED);

        order.markPaid();
        assertThat(order.status()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    @DisplayName("Должен фиксировать ошибку оплаты после начала оплаты")
    void shouldMarkPaymentAsFailed() {
        Order order = order();

        order.reserveInventory();
        order.markPaymentStarted();
        order.failPayment();

        assertThat(order.status()).isEqualTo(OrderStatus.PAYMENT_FAILED);
    }

    @Test
    @DisplayName("Должен отменять неоплаченный заказ")
    void shouldCancelUnpaidOrder() {
        Order order = order();

        order.cancel();

        assertThat(order.status()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    @DisplayName("Должен запрещать создание пустого заказа")
    void shouldRejectEmptyOrder() {
        assertThatThrownBy(() -> Order.create(OrderId.newId(), customerId(), List.of()))
                .isInstanceOf(OrderDomainException.class)
                .hasMessage("Заказ не может быть пустым");
    }

    @Test
    @DisplayName("Должен запрещать null в обязательных данных заказа")
    void shouldRejectNullOrderData() {
        OrderLine line = orderLine("10.00", 1);

        assertThatThrownBy(() -> Order.create(null, customerId(), List.of(line)))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Идентификатор заказа не может быть null");
        assertThatThrownBy(() -> Order.create(OrderId.newId(), null, List.of(line)))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Идентификатор клиента не может быть null");
        assertThatThrownBy(() -> Order.create(OrderId.newId(), customerId(), null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Строки заказа не могут быть null");
        List<OrderLine> linesWithNull = new ArrayList<>();
        linesWithNull.add(null);
        assertThatThrownBy(() -> Order.create(OrderId.newId(), customerId(), linesWithNull))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Строка заказа не может быть null");
    }

    @Test
    @DisplayName("Должен запрещать повторное резервирование")
    void shouldRejectRepeatedInventoryReservation() {
        Order order = order();

        order.reserveInventory();

        assertThatThrownBy(order::reserveInventory)
                .isInstanceOf(OrderDomainException.class)
                .hasMessage("Повторное резервирование заказа запрещено");
    }

    @Test
    @DisplayName("Должен запрещать некорректные переходы статусов оплаты")
    void shouldRejectInvalidPaymentTransitions() {
        Order order = order();

        assertThatThrownBy(order::markPaymentStarted)
                .isInstanceOf(OrderDomainException.class)
                .hasMessage("Оплату можно начать только после резервирования товаров");
        assertThatThrownBy(order::markPaid)
                .isInstanceOf(OrderDomainException.class)
                .hasMessage("Оплаченным можно сделать только заказ с начатой оплатой");
        assertThatThrownBy(order::failPayment)
                .isInstanceOf(OrderDomainException.class)
                .hasMessage("Ошибку оплаты можно зафиксировать только для начатой оплаты");
    }

    @Test
    @DisplayName("Должен запрещать отмену оплаченного заказа")
    void shouldRejectPaidOrderCancellation() {
        Order order = order();
        order.reserveInventory();
        order.markPaymentStarted();
        order.markPaid();

        assertThatThrownBy(order::cancel)
                .isInstanceOf(OrderDomainException.class)
                .hasMessage("Отмена оплаченного заказа запрещена");
    }

    @Test
    @DisplayName("Должен запрещать повторную отмену заказа")
    void shouldRejectRepeatedCancellation() {
        Order order = order();
        order.cancel();

        assertThatThrownBy(order::cancel)
                .isInstanceOf(OrderDomainException.class)
                .hasMessage("Заказ уже отменен");
    }

    @Test
    @DisplayName("Должен защищать коллекции строк и доменных событий от внешнего изменения")
    void shouldProtectInternalCollections() {
        Order order = order();

        assertThatThrownBy(() -> order.lines().add(orderLine("1.00", 1)))
                .isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> order.domainEvents().add(orderPlacedEvent(order.id())))
                .isInstanceOf(UnsupportedOperationException.class);

        order.clearDomainEvents();

        assertThat(order.domainEvents()).isEmpty();
    }

    @Test
    @DisplayName("Должен создавать доменное исключение с причиной")
    void shouldCreateDomainExceptionWithCause() {
        IllegalArgumentException cause = new IllegalArgumentException("Исходная ошибка");

        OrderDomainException exception = new OrderDomainException("Доменная ошибка", cause);

        assertThat(exception)
                .hasMessage("Доменная ошибка")
                .hasCause(cause);
    }

    @Test
    @DisplayName("Должен запрещать null в идентификаторах и событии размещения")
    void shouldRejectNullIdentifiersAndEventValues() {
        assertThatThrownBy(() -> new OrderId(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Идентификатор заказа не может быть null");
        assertThatThrownBy(() -> new CustomerId(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Идентификатор клиента не может быть null");
        assertThatThrownBy(() -> new OrderPlacedEvent(null, java.time.Instant.now()))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Идентификатор заказа в событии не может быть null");
        assertThatThrownBy(() -> new OrderPlacedEvent(OrderId.newId(), null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Момент события не может быть null");
    }

    /**
     * Создает тестовый заказ с одной строкой.
     *
     * @return тестовый заказ
     */
    private static Order order() {
        return Order.create(OrderId.newId(), customerId(), List.of(orderLine("10.00", 1)));
    }

    /**
     * Создает тестовую строку заказа.
     *
     * @param amount цена единицы товара
     * @param quantity количество товара
     * @return тестовая строка заказа
     */
    private static OrderLine orderLine(String amount, int quantity) {
        return new OrderLine(productId(), new Quantity(quantity), money(amount));
    }

    /**
     * Создает тестовую денежную сумму в евро.
     *
     * @param amount числовое значение суммы
     * @return денежная сумма
     */
    private static Money money(String amount) {
        return new Money(new BigDecimal(amount), Currency.getInstance("EUR"));
    }

    /**
     * Создает тестовый идентификатор клиента.
     *
     * @return идентификатор клиента
     */
    private static CustomerId customerId() {
        return new CustomerId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
    }

    /**
     * Создает тестовый идентификатор товара.
     *
     * @return идентификатор товара
     */
    private static ProductId productId() {
        return new ProductId(UUID.randomUUID());
    }

    /**
     * Создает тестовое событие размещения заказа.
     *
     * @param orderId идентификатор заказа
     * @return событие размещения заказа
     */
    private static DomainEvent orderPlacedEvent(OrderId orderId) {
        return new OrderPlacedEvent(orderId, java.time.Instant.now());
    }
}
