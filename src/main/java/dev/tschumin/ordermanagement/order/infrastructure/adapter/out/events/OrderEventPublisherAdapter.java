package dev.tschumin.ordermanagement.order.infrastructure.adapter.out.events;

import dev.tschumin.ordermanagement.order.application.port.out.DomainEventPublisherPort;
import dev.tschumin.ordermanagement.order.domain.event.DomainEvent;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Адаптер публикации событий заказа во внешнюю инфраструктуру.
 */
@Component
public class OrderEventPublisherAdapter implements DomainEventPublisherPort {

    /**
     * Публикует доменное событие.
     *
     * @param event доменное событие
     */
    @Override
    public void publish(DomainEvent event) {
        Objects.requireNonNull(event, "Доменное событие не может быть null");
    }
}
