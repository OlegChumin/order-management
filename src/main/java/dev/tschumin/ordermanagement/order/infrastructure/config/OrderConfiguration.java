package dev.tschumin.ordermanagement.order.infrastructure.config;

import dev.tschumin.ordermanagement.order.application.port.in.CancelOrderUseCase;
import dev.tschumin.ordermanagement.order.application.port.in.GetOrderUseCase;
import dev.tschumin.ordermanagement.order.application.port.in.PlaceOrderUseCase;
import dev.tschumin.ordermanagement.order.application.port.out.DomainEventPublisherPort;
import dev.tschumin.ordermanagement.order.application.port.out.InventoryPort;
import dev.tschumin.ordermanagement.order.application.port.out.LoadOrderPort;
import dev.tschumin.ordermanagement.order.application.port.out.PaymentPort;
import dev.tschumin.ordermanagement.order.application.port.out.SaveOrderPort;
import dev.tschumin.ordermanagement.order.application.usecase.CancelOrderHandler;
import dev.tschumin.ordermanagement.order.application.usecase.GetOrderHandler;
import dev.tschumin.ordermanagement.order.application.usecase.PlaceOrderHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация контекста заказов.
 */
@Configuration
public class OrderConfiguration {

    /**
     * Создает входной порт размещения заказа.
     *
     * @param saveOrderPort порт сохранения заказа
     * @param inventoryPort порт складских остатков
     * @param paymentPort порт оплаты
     * @param domainEventPublisherPort порт публикации доменных событий
     * @return входной порт размещения заказа
     */
    @Bean
    public PlaceOrderUseCase placeOrderUseCase(
            SaveOrderPort saveOrderPort,
            InventoryPort inventoryPort,
            PaymentPort paymentPort,
            DomainEventPublisherPort domainEventPublisherPort
    ) {
        return new PlaceOrderHandler(saveOrderPort, inventoryPort, paymentPort, domainEventPublisherPort);
    }

    /**
     * Создает входной порт получения заказа.
     *
     * @param loadOrderPort порт загрузки заказа
     * @return входной порт получения заказа
     */
    @Bean
    public GetOrderUseCase getOrderUseCase(LoadOrderPort loadOrderPort) {
        return new GetOrderHandler(loadOrderPort);
    }

    /**
     * Создает входной порт отмены заказа.
     *
     * @param loadOrderPort порт загрузки заказа
     * @param saveOrderPort порт сохранения заказа
     * @return входной порт отмены заказа
     */
    @Bean
    public CancelOrderUseCase cancelOrderUseCase(LoadOrderPort loadOrderPort, SaveOrderPort saveOrderPort) {
        return new CancelOrderHandler(loadOrderPort, saveOrderPort);
    }
}
