package dev.tschumin.ordermanagement.order.application.port.out;

import dev.tschumin.ordermanagement.order.domain.event.DomainEvent;

/**
 * Выходной порт публикации доменных событий.
 */
public interface DomainEventPublisherPort {

    /**
     * Публикует доменное событие.
     *
     * @param event доменное событие
     */
    void publish(DomainEvent event);
}
