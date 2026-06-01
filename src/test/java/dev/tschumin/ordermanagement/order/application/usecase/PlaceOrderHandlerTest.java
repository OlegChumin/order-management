package dev.tschumin.ordermanagement.order.application.usecase;

import dev.tschumin.ordermanagement.order.application.command.PlaceOrderCommand;
import dev.tschumin.ordermanagement.order.application.command.PlaceOrderLineCommand;
import dev.tschumin.ordermanagement.order.application.port.out.DomainEventPublisherPort;
import dev.tschumin.ordermanagement.order.application.port.out.InventoryPort;
import dev.tschumin.ordermanagement.order.application.port.out.PaymentPort;
import dev.tschumin.ordermanagement.order.application.port.out.SaveOrderPort;
import dev.tschumin.ordermanagement.order.domain.event.DomainEvent;
import dev.tschumin.ordermanagement.order.domain.model.Order;
import dev.tschumin.ordermanagement.order.domain.valueobject.CustomerId;
import dev.tschumin.ordermanagement.order.domain.valueobject.Money;
import dev.tschumin.ordermanagement.order.domain.valueobject.OrderStatus;
import dev.tschumin.ordermanagement.order.domain.valueobject.ProductId;
import dev.tschumin.ordermanagement.order.domain.valueobject.Quantity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;

/**
 * Тесты обработчика размещения заказа.
 *
 * <ul>
 *     <li>Проверяет создание заказа через доменную модель.</li>
 *     <li>Проверяет вызовы портов складского резерва, оплаты, сохранения и публикации событий.</li>
 *     <li>Проверяет ошибку при отсутствии команды.</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
class PlaceOrderHandlerTest {

    /**
     * Порт сохранения заказа.
     */
    @Mock
    private SaveOrderPort saveOrderPort;

    /**
     * Порт складских остатков.
     */
    @Mock
    private InventoryPort inventoryPort;

    /**
     * Порт оплаты.
     */
    @Mock
    private PaymentPort paymentPort;

    /**
     * Порт публикации доменных событий.
     */
    @Mock
    private DomainEventPublisherPort domainEventPublisherPort;

    @Test
    @DisplayName("Должен разместить заказ и вызвать внешние порты")
    void shouldPlaceOrderAndCallPorts() {
        PlaceOrderHandler handler = handler();

        Order order = handler.placeOrder(command());

        assertThat(order.status()).isEqualTo(OrderStatus.PAYMENT_STARTED);
        assertThat(order.inventoryReserved()).isTrue();
        assertThat(order.totalPrice()).isEqualTo(money("20.00"));
        assertThat(order.domainEvents()).isEmpty();

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(inventoryPort).reserve(orderCaptor.capture());
        verify(paymentPort).startPayment(order.id(), money("20.00"));
        verify(saveOrderPort).save(order);
        verify(domainEventPublisherPort).publish(any(DomainEvent.class));
        assertThat(orderCaptor.getValue()).isSameAs(order);

        InOrder inOrder = inOrder(inventoryPort, paymentPort, saveOrderPort, domainEventPublisherPort);
        inOrder.verify(inventoryPort).reserve(any(Order.class));
        inOrder.verify(paymentPort).startPayment(order.id(), money("20.00"));
        inOrder.verify(saveOrderPort).save(order);
        inOrder.verify(domainEventPublisherPort).publish(any(DomainEvent.class));
    }

    @Test
    @DisplayName("Должен отклонить null вместо команды размещения")
    void shouldRejectNullCommand() {
        PlaceOrderHandler handler = handler();

        assertThatThrownBy(() -> handler.placeOrder(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Команда размещения заказа не может быть null");
    }

    /**
     * Создает тестируемый обработчик размещения заказа.
     *
     * @return обработчик размещения заказа
     */
    private PlaceOrderHandler handler() {
        return new PlaceOrderHandler(saveOrderPort, inventoryPort, paymentPort, domainEventPublisherPort);
    }

    /**
     * Создает команду размещения заказа.
     *
     * @return команда размещения заказа
     */
    private PlaceOrderCommand command() {
        return new PlaceOrderCommand(
                new CustomerId(UUID.fromString("00000000-0000-0000-0000-000000000001")),
                List.of(new PlaceOrderLineCommand(
                        new ProductId(UUID.fromString("00000000-0000-0000-0000-000000000002")),
                        new Quantity(2),
                        money("10.00")
                ))
        );
    }

    /**
     * Создает денежную сумму в евро.
     *
     * @param amount числовое значение суммы
     * @return денежная сумма
     */
    private Money money(String amount) {
        return new Money(new BigDecimal(amount), Currency.getInstance("EUR"));
    }
}
