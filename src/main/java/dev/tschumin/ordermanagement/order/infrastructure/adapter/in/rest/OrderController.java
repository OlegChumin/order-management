package dev.tschumin.ordermanagement.order.infrastructure.adapter.in.rest;

import dev.tschumin.ordermanagement.order.application.command.CancelOrderCommand;
import dev.tschumin.ordermanagement.order.application.port.in.CancelOrderUseCase;
import dev.tschumin.ordermanagement.order.application.port.in.GetOrderUseCase;
import dev.tschumin.ordermanagement.order.application.port.in.PlaceOrderUseCase;
import dev.tschumin.ordermanagement.order.domain.model.Order;
import dev.tschumin.ordermanagement.order.domain.valueobject.OrderId;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;
import java.util.UUID;

/**
 * REST-контроллер заказов.
 */
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    /**
     * Входной порт размещения заказа.
     */
    private final PlaceOrderUseCase placeOrderUseCase;

    /**
     * Входной порт получения заказа.
     */
    private final GetOrderUseCase getOrderUseCase;

    /**
     * Входной порт отмены заказа.
     */
    private final CancelOrderUseCase cancelOrderUseCase;

    /**
     * Маппер REST DTO заказов.
     */
    private final OrderRestMapper mapper;

    /**
     * Создает REST-контроллер заказов.
     *
     * @param placeOrderUseCase входной порт размещения заказа
     * @param getOrderUseCase входной порт получения заказа
     * @param cancelOrderUseCase входной порт отмены заказа
     * @param mapper маппер REST DTO заказов
     */
    public OrderController(
            PlaceOrderUseCase placeOrderUseCase,
            GetOrderUseCase getOrderUseCase,
            CancelOrderUseCase cancelOrderUseCase,
            OrderRestMapper mapper
    ) {
        this.placeOrderUseCase = Objects.requireNonNull(placeOrderUseCase, "Use case размещения заказа не может быть null");
        this.getOrderUseCase = Objects.requireNonNull(getOrderUseCase, "Use case получения заказа не может быть null");
        this.cancelOrderUseCase = Objects.requireNonNull(cancelOrderUseCase, "Use case отмены заказа не может быть null");
        this.mapper = Objects.requireNonNull(mapper, "REST-маппер заказов не может быть null");
    }

    /**
     * Создает заказ.
     *
     * @param request REST-запрос создания заказа
     * @return REST-ответ с созданным заказом
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(@Valid @RequestBody CreateOrderRequest request) {
        Order order = placeOrderUseCase.placeOrder(mapper.toCommand(request));
        return mapper.toResponse(order);
    }

    /**
     * Возвращает заказ по идентификатору.
     *
     * @param orderId идентификатор заказа
     * @return REST-ответ с заказом
     */
    @GetMapping("/{orderId}")
    public OrderResponse getOrder(@PathVariable UUID orderId) {
        Order order = getOrderUseCase.getOrder(new OrderId(orderId));
        return mapper.toResponse(order);
    }

    /**
     * Отменяет заказ по идентификатору.
     *
     * @param orderId идентификатор заказа
     */
    @PostMapping("/{orderId}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelOrder(@PathVariable UUID orderId) {
        cancelOrderUseCase.cancelOrder(new CancelOrderCommand(new OrderId(orderId)));
    }
}
