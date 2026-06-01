package dev.tschumin.ordermanagement.order.domain.event;

import java.time.Instant;

/**
 * Доменное событие, возникшее внутри модели предметной области.
 */
public interface DomainEvent {

    /**
     * Возвращает момент возникновения доменного события.
     *
     * @return момент возникновения события
     */
    Instant occurredAt();
}
