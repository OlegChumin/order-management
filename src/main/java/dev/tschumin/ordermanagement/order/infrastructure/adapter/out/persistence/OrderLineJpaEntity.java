package dev.tschumin.ordermanagement.order.infrastructure.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;
import java.util.Objects;
import java.util.UUID;

/**
 * JPA-сущность строки заказа для таблицы {@code order_lines}.
 */
@Entity
@Table(
        name = "order_lines",
        indexes = {
                @Index(name = "idx_order_lines_order_id", columnList = "order_id"),
                @Index(name = "idx_order_lines_product_id", columnList = "product_id")
        }
)
public class OrderLineJpaEntity {

    /**
     * Идентификатор строки заказа.
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
     * Заказ, которому принадлежит строка.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "order_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_order_lines_order_id")
    )
    private OrderJpaEntity order;

    /**
     * Идентификатор товара.
     */
    @Column(name = "product_id", nullable = false)
    private UUID productId;

    /**
     * Количество товара.
     */
    @Column(name = "quantity", nullable = false)
    private int quantity;

    /**
     * Цена единицы товара.
     */
    @Column(name = "unit_price_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal unitPriceAmount;

    /**
     * Валюта цены единицы товара.
     */
    @Column(name = "unit_price_currency", nullable = false, length = 3)
    private String unitPriceCurrency;

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
     * Создает пустую JPA-сущность строки заказа для ORM.
     */
    protected OrderLineJpaEntity() {
    }

    /**
     * Создает JPA-сущность строки заказа.
     *
     * @param id идентификатор строки заказа
     * @param productId идентификатор товара
     * @param quantity количество товара
     * @param unitPriceAmount цена единицы товара
     * @param unitPriceCurrency валюта цены единицы товара
     */
    public OrderLineJpaEntity(
            UUID id,
            UUID productId,
            int quantity,
            BigDecimal unitPriceAmount,
            Currency unitPriceCurrency
    ) {
        this.id = Objects.requireNonNull(id, "Идентификатор строки заказа не может быть null");
        this.productId = Objects.requireNonNull(productId, "Идентификатор товара не может быть null");
        this.quantity = quantity;
        this.unitPriceAmount = Objects.requireNonNull(unitPriceAmount, "Цена единицы товара не может быть null");
        this.unitPriceCurrency = Objects.requireNonNull(unitPriceCurrency, "Валюта цены не может быть null")
                .getCurrencyCode();
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
     * Привязывает строку заказа к JPA-сущности заказа.
     *
     * @param order заказ-владелец строки
     */
    void attachTo(OrderJpaEntity order) {
        this.order = Objects.requireNonNull(order, "Заказ строки не может быть null");
    }

    /**
     * Возвращает идентификатор строки заказа.
     *
     * @return идентификатор строки заказа
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
     * Возвращает заказ-владелец строки.
     *
     * @return заказ-владелец строки
     */
    public OrderJpaEntity order() {
        return order;
    }

    /**
     * Возвращает идентификатор товара.
     *
     * @return идентификатор товара
     */
    public UUID productId() {
        return productId;
    }

    /**
     * Возвращает количество товара.
     *
     * @return количество товара
     */
    public int quantity() {
        return quantity;
    }

    /**
     * Возвращает цену единицы товара.
     *
     * @return цена единицы товара
     */
    public BigDecimal unitPriceAmount() {
        return unitPriceAmount;
    }

    /**
     * Возвращает валюту цены единицы товара.
     *
     * @return валюта цены единицы товара
     */
    public String unitPriceCurrency() {
        return unitPriceCurrency;
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
}
