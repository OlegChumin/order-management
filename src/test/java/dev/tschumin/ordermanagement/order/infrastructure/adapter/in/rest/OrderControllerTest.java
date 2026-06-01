package dev.tschumin.ordermanagement.order.infrastructure.adapter.in.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.tschumin.ordermanagement.order.application.command.CancelOrderCommand;
import dev.tschumin.ordermanagement.order.application.exception.OrderNotFoundException;
import dev.tschumin.ordermanagement.order.application.port.in.CancelOrderUseCase;
import dev.tschumin.ordermanagement.order.application.port.in.GetOrderUseCase;
import dev.tschumin.ordermanagement.order.application.port.in.PlaceOrderUseCase;
import dev.tschumin.ordermanagement.order.domain.model.Order;
import dev.tschumin.ordermanagement.order.domain.model.OrderLine;
import dev.tschumin.ordermanagement.order.domain.valueobject.CustomerId;
import dev.tschumin.ordermanagement.order.domain.valueobject.Money;
import dev.tschumin.ordermanagement.order.domain.valueobject.OrderId;
import dev.tschumin.ordermanagement.order.domain.valueobject.ProductId;
import dev.tschumin.ordermanagement.order.domain.valueobject.Quantity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Тесты REST-контроллера заказов.
 *
 * <ul>
 *     <li>Проверяет создание заказа через POST endpoint.</li>
 *     <li>Проверяет получение заказа через GET endpoint.</li>
 *     <li>Проверяет отмену заказа через cancel endpoint.</li>
 *     <li>Проверяет обработку ошибок валидации и отсутствующего заказа.</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    /**
     * Входной порт размещения заказа.
     */
    @Mock
    private PlaceOrderUseCase placeOrderUseCase;

    /**
     * Входной порт получения заказа.
     */
    @Mock
    private GetOrderUseCase getOrderUseCase;

    /**
     * Входной порт отмены заказа.
     */
    @Mock
    private CancelOrderUseCase cancelOrderUseCase;

    /**
     * MockMvc для проверки REST-контроллера.
     */
    private MockMvc mockMvc;

    /**
     * JSON mapper для REST-запросов.
     */
    private ObjectMapper objectMapper;

    /**
     * Настраивает standalone MockMvc перед каждым тестом.
     */
    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders
                .standaloneSetup(new OrderController(
                        placeOrderUseCase,
                        getOrderUseCase,
                        cancelOrderUseCase,
                        new OrderRestMapper()
                ))
                .setControllerAdvice(new OrderRestExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    @DisplayName("Должен создать заказ через REST endpoint")
    void shouldCreateOrder() throws Exception {
        Order order = order();
        when(placeOrderUseCase.placeOrder(any())).thenReturn(order);

        mockMvc.perform(post("/api/v1/orders")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(createRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(order.id().value().toString()))
                .andExpect(jsonPath("$.customerId").value(order.customerId().value().toString()))
                .andExpect(jsonPath("$.status").value(order.status().name()))
                .andExpect(jsonPath("$.totalAmount").value(20.00));
    }

    @Test
    @DisplayName("Должен получить заказ через REST endpoint")
    void shouldGetOrder() throws Exception {
        Order order = order();
        when(getOrderUseCase.getOrder(order.id())).thenReturn(order);

        mockMvc.perform(get("/api/v1/orders/{orderId}", order.id().value()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(order.id().value().toString()))
                .andExpect(jsonPath("$.lines[0].productId").value(order.lines().getFirst().productId().value().toString()));
    }

    @Test
    @DisplayName("Должен отменить заказ через REST endpoint")
    void shouldCancelOrder() throws Exception {
        UUID orderId = UUID.fromString("00000000-0000-0000-0000-000000000010");

        mockMvc.perform(post("/api/v1/orders/{orderId}/cancel", orderId))
                .andExpect(status().isNoContent());

        ArgumentCaptor<CancelOrderCommand> commandCaptor = ArgumentCaptor.forClass(CancelOrderCommand.class);
        verify(cancelOrderUseCase).cancelOrder(commandCaptor.capture());
        assertThat(commandCaptor.getValue().orderId().value()).isEqualTo(orderId);
    }

    @Test
    @DisplayName("Должен вернуть ошибку валидации для некорректного запроса")
    void shouldReturnValidationError() throws Exception {
        Map<String, Object> request = Map.of("customerId", UUID.randomUUID(), "lines", List.of());

        mockMvc.perform(post("/api/v1/orders")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Ошибка валидации"))
                .andExpect(jsonPath("$.details[0]").exists());
    }

    @Test
    @DisplayName("Должен вернуть 404 для отсутствующего заказа")
    void shouldReturnNotFound() throws Exception {
        OrderId orderId = OrderId.newId();
        when(getOrderUseCase.getOrder(orderId)).thenThrow(new OrderNotFoundException(orderId));

        mockMvc.perform(get("/api/v1/orders/{orderId}", orderId.value()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Заказ не найден"));
    }

    /**
     * Создает REST-запрос создания заказа.
     *
     * @return REST-запрос создания заказа
     */
    private CreateOrderRequest createRequest() {
        return new CreateOrderRequest(
                UUID.fromString("00000000-0000-0000-0000-000000000001"),
                List.of(new CreateOrderLineRequest(
                        UUID.fromString("00000000-0000-0000-0000-000000000002"),
                        2,
                        new BigDecimal("10.00"),
                        "EUR"
                ))
        );
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
                List.of(new OrderLine(
                        new ProductId(UUID.fromString("00000000-0000-0000-0000-000000000002")),
                        new Quantity(2),
                        new Money(new BigDecimal("10.00"), java.util.Currency.getInstance("EUR"))
                ))
        );
    }
}
