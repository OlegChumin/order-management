package dev.tschumin.ordermanagement.order.infrastructure.adapter.out.persistence;

import dev.tschumin.ordermanagement.order.domain.model.Order;
import dev.tschumin.ordermanagement.order.domain.model.OrderLine;
import dev.tschumin.ordermanagement.order.domain.valueobject.CustomerId;
import dev.tschumin.ordermanagement.order.domain.valueobject.Money;
import dev.tschumin.ordermanagement.order.domain.valueobject.OrderId;
import dev.tschumin.ordermanagement.order.domain.valueobject.ProductId;
import dev.tschumin.ordermanagement.order.domain.valueobject.Quantity;
import org.springframework.stereotype.Component;

import java.util.Currency;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Маппер между доменной моделью заказа и JPA-сущностями.
 */
@Component
public class OrderPersistenceMapper {

    /**
     * Преобразует доменный заказ в JPA-сущность.
     *
     * @param order доменный заказ
     * @return JPA-сущность заказа
     */
    public OrderJpaEntity toEntity(Order order) {
        Objects.requireNonNull(order, "Заказ не может быть null");

        OrderJpaEntity entity = new OrderJpaEntity(
                order.id().value(),
                order.customerId().value(),
                order.status(),
                order.inventoryReserved()
        );
        order.lines().stream()
                .map(this::toEntity)
                .forEach(entity::addLine);
        return entity;
    }

    /**
     * Обновляет существующую JPA-сущность данными доменного заказа.
     *
     * @param order доменный заказ
     * @param entity существующая JPA-сущность заказа
     */
    public void updateEntity(Order order, OrderJpaEntity entity) {
        Objects.requireNonNull(order, "Заказ не может быть null");
        Objects.requireNonNull(entity, "JPA-сущность заказа не может быть null");

        entity.updateState(order.customerId().value(), order.status(), order.inventoryReserved());
        entity.replaceLines(order.lines().stream()
                .map(this::toEntity)
                .toList());
    }

    /**
     * Преобразует JPA-сущность заказа в доменную модель.
     *
     * @param entity JPA-сущность заказа
     * @return доменный заказ
     */
    public Order toDomain(OrderJpaEntity entity) {
        Objects.requireNonNull(entity, "JPA-сущность заказа не может быть null");

        return Order.restore(
                new OrderId(entity.id()),
                new CustomerId(entity.customerId()),
                toDomainLines(entity.lines()),
                entity.status(),
                entity.inventoryReserved()
        );
    }

    /**
     * Преобразует доменную строку заказа в JPA-сущность.
     *
     * @param line доменная строка заказа
     * @return JPA-сущность строки заказа
     */
    private OrderLineJpaEntity toEntity(OrderLine line) {
        Objects.requireNonNull(line, "Строка заказа не может быть null");
        return new OrderLineJpaEntity(
                UUID.randomUUID(),
                line.productId().value(),
                line.quantity().value(),
                line.unitPrice().amount(),
                line.unitPrice().currency()
        );
    }

    /**
     * Преобразует JPA-строки заказа в доменные строки.
     *
     * @param lines JPA-строки заказа
     * @return доменные строки заказа
     */
    private List<OrderLine> toDomainLines(List<OrderLineJpaEntity> lines) {
        Objects.requireNonNull(lines, "JPA-строки заказа не могут быть null");
        return lines.stream()
                .map(this::toDomain)
                .toList();
    }

    /**
     * Преобразует JPA-сущность строки заказа в доменную строку.
     *
     * @param entity JPA-сущность строки заказа
     * @return доменная строка заказа
     */
    private OrderLine toDomain(OrderLineJpaEntity entity) {
        Objects.requireNonNull(entity, "JPA-сущность строки заказа не может быть null");
        return new OrderLine(
                new ProductId(entity.productId()),
                new Quantity(entity.quantity()),
                new Money(entity.unitPriceAmount(), Currency.getInstance(entity.unitPriceCurrency()))
        );
    }
}
