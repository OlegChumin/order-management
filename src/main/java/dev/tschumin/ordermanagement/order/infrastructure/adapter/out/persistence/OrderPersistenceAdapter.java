package dev.tschumin.ordermanagement.order.infrastructure.adapter.out.persistence;

import dev.tschumin.ordermanagement.order.application.port.out.LoadOrderPort;
import dev.tschumin.ordermanagement.order.application.port.out.SaveOrderPort;
import dev.tschumin.ordermanagement.order.domain.model.Order;
import dev.tschumin.ordermanagement.order.domain.valueobject.OrderId;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

/**
 * Адаптер постоянного хранения заказов.
 */
@Repository
public class OrderPersistenceAdapter implements SaveOrderPort, LoadOrderPort {

    /**
     * Spring Data JPA репозиторий заказов.
     */
    private final SpringDataOrderJpaRepository repository;

    /**
     * Маппер между доменной моделью и JPA-моделью.
     */
    private final OrderPersistenceMapper mapper;

    /**
     * Создает адаптер постоянного хранения заказов.
     *
     * @param repository Spring Data JPA репозиторий заказов
     * @param mapper маппер между доменной моделью и JPA-моделью
     */
    public OrderPersistenceAdapter(SpringDataOrderJpaRepository repository, OrderPersistenceMapper mapper) {
        this.repository = Objects.requireNonNull(repository, "JPA репозиторий заказов не может быть null");
        this.mapper = Objects.requireNonNull(mapper, "Маппер заказов не может быть null");
    }

    /**
     * Сохраняет доменный заказ через JPA-модель.
     *
     * @param order заказ для сохранения
     */
    @Override
    @Transactional
    public void save(Order order) {
        Objects.requireNonNull(order, "Заказ не может быть null");
        repository.findById(order.id().value())
                .ifPresentOrElse(
                        entity -> mapper.updateEntity(order, entity),
                        () -> repository.save(mapper.toEntity(order))
                );
    }

    /**
     * Загружает доменный заказ по идентификатору.
     *
     * @param orderId идентификатор заказа
     * @return найденный заказ
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Order> load(OrderId orderId) {
        Objects.requireNonNull(orderId, "Идентификатор заказа не может быть null");
        return repository.findById(orderId.value())
                .map(mapper::toDomain);
    }
}
