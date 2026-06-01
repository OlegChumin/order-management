package dev.tschumin.ordermanagement.order.infrastructure.adapter.out.persistence;

import dev.tschumin.ordermanagement.order.domain.model.Order;
import dev.tschumin.ordermanagement.order.domain.model.OrderLine;
import dev.tschumin.ordermanagement.order.domain.valueobject.CustomerId;
import dev.tschumin.ordermanagement.order.domain.valueobject.Money;
import dev.tschumin.ordermanagement.order.domain.valueobject.OrderId;
import dev.tschumin.ordermanagement.order.domain.valueobject.OrderStatus;
import dev.tschumin.ordermanagement.order.domain.valueobject.ProductId;
import dev.tschumin.ordermanagement.order.domain.valueobject.Quantity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Тесты маппера постоянного хранения заказов.
 *
 * <ul>
 *     <li>Проверяет преобразование доменного заказа в JPA-сущность.</li>
 *     <li>Проверяет преобразование JPA-сущности в доменный заказ.</li>
 *     <li>Проверяет обновление существующей JPA-сущности.</li>
 *     <li>Проверяет ошибки при отсутствии обязательных аргументов.</li>
 * </ul>
 */
class OrderPersistenceMapperTest {

    /**
     * Маппер постоянного хранения заказов.
     */
    private final OrderPersistenceMapper mapper = new OrderPersistenceMapper();

    @Test
    @DisplayName("Должен преобразовать доменный заказ в JPA-сущность")
    void shouldMapDomainOrderToEntity() {
        Order order = order();

        OrderJpaEntity entity = mapper.toEntity(order);

        assertThat(entity.id()).isEqualTo(order.id().value());
        assertThat(entity.customerId()).isEqualTo(order.customerId().value());
        assertThat(entity.status()).isEqualTo(order.status());
        assertThat(entity.inventoryReserved()).isEqualTo(order.inventoryReserved());
        assertThat(entity.lines()).hasSize(1);
        assertThat(entity.lines().getFirst().order()).isSameAs(entity);
        assertThat(entity.lines().getFirst().productId()).isEqualTo(order.lines().getFirst().productId().value());
    }

    @Test
    @DisplayName("Должен преобразовать JPA-сущность в доменный заказ")
    void shouldMapEntityToDomainOrder() {
        OrderJpaEntity entity = entity();

        Order order = mapper.toDomain(entity);

        assertThat(order.id().value()).isEqualTo(entity.id());
        assertThat(order.customerId().value()).isEqualTo(entity.customerId());
        assertThat(order.status()).isEqualTo(entity.status());
        assertThat(order.inventoryReserved()).isEqualTo(entity.inventoryReserved());
        assertThat(order.lines()).hasSize(1);
        assertThat(order.lines().getFirst().productId().value()).isEqualTo(entity.lines().getFirst().productId());
        assertThat(order.domainEvents()).isEmpty();
    }

    @Test
    @DisplayName("Должен обновить существующую JPA-сущность")
    void shouldUpdateExistingEntity() {
        OrderJpaEntity entity = entity();
        Order order = Order.restore(
                new OrderId(entity.id()),
                new CustomerId(UUID.fromString("00000000-0000-0000-0000-000000000011")),
                List.of(orderLine("20.00", 2)),
                OrderStatus.PAYMENT_STARTED,
                true
        );

        mapper.updateEntity(order, entity);

        assertThat(entity.customerId()).isEqualTo(order.customerId().value());
        assertThat(entity.status()).isEqualTo(OrderStatus.PAYMENT_STARTED);
        assertThat(entity.inventoryReserved()).isTrue();
        assertThat(entity.lines()).hasSize(1);
        assertThat(entity.lines().getFirst().order()).isSameAs(entity);
    }

    @Test
    @DisplayName("Должен отклонить null аргументы")
    void shouldRejectNullArguments() {
        assertThatThrownBy(() -> mapper.toEntity(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Заказ не может быть null");
        assertThatThrownBy(() -> mapper.toDomain(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("JPA-сущность заказа не может быть null");
        assertThatThrownBy(() -> mapper.updateEntity(order(), null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("JPA-сущность заказа не может быть null");
    }

    /**
     * Создает тестовый доменный заказ.
     *
     * @return тестовый доменный заказ
     */
    private Order order() {
        return Order.create(
                OrderId.newId(),
                new CustomerId(UUID.fromString("00000000-0000-0000-0000-000000000001")),
                List.of(orderLine("10.00", 1))
        );
    }

    /**
     * Создает тестовую JPA-сущность заказа.
     *
     * @return JPA-сущность заказа
     */
    private OrderJpaEntity entity() {
        OrderJpaEntity entity = new OrderJpaEntity(
                UUID.fromString("00000000-0000-0000-0000-000000000021"),
                UUID.fromString("00000000-0000-0000-0000-000000000022"),
                OrderStatus.INVENTORY_RESERVED,
                true
        );
        entity.addLine(new OrderLineJpaEntity(
                UUID.fromString("00000000-0000-0000-0000-000000000023"),
                UUID.fromString("00000000-0000-0000-0000-000000000024"),
                2,
                new BigDecimal("5.00"),
                Currency.getInstance("EUR")
        ));
        return entity;
    }

    /**
     * Создает тестовую доменную строку заказа.
     *
     * @param amount цена единицы товара
     * @param quantity количество товара
     * @return доменная строка заказа
     */
    private OrderLine orderLine(String amount, int quantity) {
        return new OrderLine(
                new ProductId(UUID.randomUUID()),
                new Quantity(quantity),
                new Money(new BigDecimal(amount), Currency.getInstance("EUR"))
        );
    }
}
