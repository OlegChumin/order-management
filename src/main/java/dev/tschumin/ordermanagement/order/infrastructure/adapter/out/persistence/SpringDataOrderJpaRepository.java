package dev.tschumin.ordermanagement.order.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Spring Data JPA репозиторий для JPA-сущностей заказов.
 */
public interface SpringDataOrderJpaRepository extends JpaRepository<OrderJpaEntity, UUID> {
}
