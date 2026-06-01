package dev.tschumin.ordermanagement.order.infrastructure.adapter.in.rest;

import dev.tschumin.ordermanagement.order.application.command.PlaceOrderCommand;
import dev.tschumin.ordermanagement.order.application.command.PlaceOrderLineCommand;
import dev.tschumin.ordermanagement.order.domain.model.Order;
import dev.tschumin.ordermanagement.order.domain.model.OrderLine;
import dev.tschumin.ordermanagement.order.domain.valueobject.CustomerId;
import dev.tschumin.ordermanagement.order.domain.valueobject.Money;
import dev.tschumin.ordermanagement.order.domain.valueobject.ProductId;
import dev.tschumin.ordermanagement.order.domain.valueobject.Quantity;
import org.springframework.stereotype.Component;

import java.util.Currency;
import java.util.List;
import java.util.Objects;

/**
 * Маппер REST DTO заказов в команды приложения и обратно в REST-ответы.
 */
@Component
public class OrderRestMapper {

    /**
     * Преобразует REST-запрос создания заказа в команду приложения.
     *
     * @param request REST-запрос создания заказа
     * @return команда размещения заказа
     */
    public PlaceOrderCommand toCommand(CreateOrderRequest request) {
        Objects.requireNonNull(request, "REST-запрос создания заказа не может быть null");
        return new PlaceOrderCommand(
                new CustomerId(request.getCustomerId()),
                request.getLines().stream()
                        .map(this::toCommand)
                        .toList()
        );
    }

    /**
     * Преобразует доменный заказ в REST-ответ.
     *
     * @param order доменный заказ
     * @return REST-ответ с заказом
     */
    public OrderResponse toResponse(Order order) {
        Objects.requireNonNull(order, "Заказ не может быть null");
        Money totalPrice = order.totalPrice();
        return new OrderResponse(
                order.id().value(),
                order.customerId().value(),
                order.status().name(),
                order.inventoryReserved(),
                order.lines().stream()
                        .map(this::toResponse)
                        .toList(),
                totalPrice.amount(),
                totalPrice.currency().getCurrencyCode()
        );
    }

    /**
     * Преобразует REST-запрос строки заказа в команду строки заказа.
     *
     * @param request REST-запрос строки заказа
     * @return команда строки заказа
     */
    private PlaceOrderLineCommand toCommand(CreateOrderLineRequest request) {
        Objects.requireNonNull(request, "REST-запрос строки заказа не может быть null");
        return new PlaceOrderLineCommand(
                new ProductId(request.getProductId()),
                new Quantity(request.getQuantity()),
                new Money(request.getUnitPriceAmount(), Currency.getInstance(request.getUnitPriceCurrency()))
        );
    }

    /**
     * Преобразует доменную строку заказа в REST-ответ.
     *
     * @param line доменная строка заказа
     * @return REST-ответ со строкой заказа
     */
    private OrderLineResponse toResponse(OrderLine line) {
        Objects.requireNonNull(line, "Строка заказа не может быть null");
        Money lineTotal = line.totalPrice();
        return new OrderLineResponse(
                line.productId().value(),
                line.quantity().value(),
                line.unitPrice().amount(),
                line.unitPrice().currency().getCurrencyCode(),
                lineTotal.amount(),
                lineTotal.currency().getCurrencyCode()
        );
    }
}
