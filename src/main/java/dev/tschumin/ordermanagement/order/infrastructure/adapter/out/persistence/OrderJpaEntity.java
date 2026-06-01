package dev.tschumin.ordermanagement.order.infrastructure.adapter.out.persistence;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import dev.tschumin.ordermanagement.order.domain.valueobject.OrderStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * JPA-сущность заказа для таблицы {@code orders}.
 */
@Entity
@Table(
        name = "orders",
        indexes = {
                @Index(name = "idx_orders_customer_id", columnList = "customer_id"),
                @Index(name = "idx_orders_status", columnList = "status")
        }
)
public class OrderJpaEntity {

    /**
     * Идентификатор заказа.
     */
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    /**
     * Версия записи для оптимистичной блокировки.
     */
    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    /**
     * Идентификатор клиента.
     */
    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    /**
     * Статус заказа.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 64)
    private OrderStatus status;

    /**
     * Признак резервирования складских остатков.
     */
    @Column(name = "inventory_reserved", nullable = false)
    private boolean inventoryReserved;

    /**
     * Строки заказа.
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderLineJpaEntity> lines = new ArrayList<>();

    /**
     * Момент создания записи.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    /**
     * Момент последнего обновления записи.
     */
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /**
     * Создает пустую JPA-сущность заказа для ORM.
     */
    protected OrderJpaEntity() {
    }

    /**
     * Создает JPA-сущность заказа.
     *
     * @param id идентификатор заказа
     * @param customerId идентификатор клиента
     * @param status статус заказа
     * @param inventoryReserved признак резервирования складских остатков
     */
    public OrderJpaEntity(UUID id, UUID customerId, OrderStatus status, boolean inventoryReserved) {
        this.id = Objects.requireNonNull(id, "Идентификатор заказа не может быть null");
        this.customerId = Objects.requireNonNull(customerId, "Идентификатор клиента не может быть null");
        this.status = Objects.requireNonNull(status, "Статус заказа не может быть null");
        this.inventoryReserved = inventoryReserved;
    }

    /**
     * Обновляет audit timestamps перед первым сохранением.
     */
    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    /**
     * Обновляет audit timestamp перед изменением записи.
     */
    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }

    /**
     * Возвращает идентификатор заказа.
     *
     * @return идентификатор заказа
     */
    public UUID id() {
        return id;
    }

    /**
     * Возвращает версию записи.
     *
     * @return версия записи
     */
    public Long version() {
        return version;
    }

    /**
     * Возвращает идентификатор клиента.
     *
     * @return идентификатор клиента
     */
    public UUID customerId() {
        return customerId;
    }

    /**
     * Возвращает статус заказа.
     *
     * @return статус заказа
     */
    public OrderStatus status() {
        return status;
    }

    /**
     * Возвращает признак резервирования складских остатков.
     *
     * @return {@code true}, если остатки зарезервированы
     */
    public boolean inventoryReserved() {
        return inventoryReserved;
    }

    /**
     * Возвращает строки заказа.
     *
     * @return строки заказа
     */
    public List<OrderLineJpaEntity> lines() {
        return lines;
    }

    /**
     * Возвращает момент создания записи.
     *
     * @return момент создания записи
     */
    public Instant createdAt() {
        return createdAt;
    }

    /**
     * Возвращает момент последнего обновления записи.
     *
     * @return момент последнего обновления записи
     */
    public Instant updatedAt() {
        return updatedAt;
    }

    /**
     * Добавляет строку заказа к JPA-сущности.
     *
     * @param line строка заказа
     */
    public void addLine(OrderLineJpaEntity line) {
        Objects.requireNonNull(line, "Строка заказа не может быть null");
        line.attachTo(this);
        lines.add(line);
    }

    /**
     * Обновляет изменяемое состояние JPA-сущности заказа.
     *
     * @param customerId идентификатор клиента
     * @param status статус заказа
     * @param inventoryReserved признак резервирования складских остатков
     */
    public void updateState(UUID customerId, OrderStatus status, boolean inventoryReserved) {
        this.customerId = Objects.requireNonNull(customerId, "Идентификатор клиента не может быть null");
        this.status = Objects.requireNonNull(status, "Статус заказа не может быть null");
        this.inventoryReserved = inventoryReserved;
    }

    /**
     * Заменяет строки заказа.
     *
     * @param newLines новые строки заказа
     */
    public void replaceLines(List<OrderLineJpaEntity> newLines) {
        Objects.requireNonNull(newLines, "Новые строки заказа не могут быть null");
        lines.clear();
        newLines.forEach(this::addLine);
    }
}
