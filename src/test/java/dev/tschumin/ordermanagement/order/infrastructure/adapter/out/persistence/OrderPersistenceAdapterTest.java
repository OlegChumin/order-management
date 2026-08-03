package dev.tschumin.ordermanagement.order.infrastructure.adapter.out.persistence;

import dev.tschumin.ordermanagement.order.domain.model.Order;
import dev.tschumin.ordermanagement.order.domain.model.OrderLine;
import dev.tschumin.ordermanagement.order.domain.valueobject.CustomerId;
import dev.tschumin.ordermanagement.order.domain.valueobject.Money;
import dev.tschumin.ordermanagement.order.domain.valueobject.OrderId;
import dev.tschumin.ordermanagement.order.domain.valueobject.OrderStatus;
import dev.tschumin.ordermanagement.order.domain.valueobject.ProductId;
import dev.tschumin.ordermanagement.order.domain.valueobject.Quantity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Интеграционные тесты адаптера постоянного хранения заказов.
 *
 * <ul>
 *     <li>Проверяет сохранение и загрузку заказа через PostgreSQL Testcontainer.</li>
 *     <li>Проверяет обновление статуса заказа с сохранением UUID идентификатора.</li>
 *     <li>Проверяет пустой результат при отсутствии заказа.</li>
 * </ul>
 */
@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({OrderPersistenceAdapter.class, OrderPersistenceMapper.class})
class OrderPersistenceAdapterTest {

    /**
     * PostgreSQL контейнер для интеграционного теста постоянного хранения.
     */
    @Container
    private static final PostgreSQLContainer<?> POSTGRESQL = new PostgreSQLContainer<>("postgres:16-alpine");

    /**
     * Адаптер постоянного хранения заказов.
     */
    @Autowired
    private OrderPersistenceAdapter adapter;

    /**
     * Spring Data JPA репозиторий заказов.
     */
    @Autowired
    private SpringDataOrderJpaRepository repository;

    /**
     * Регистрирует настройки подключения к PostgreSQL Testcontainer.
     *
     * @param registry реестр динамических свойств Spring
     */
    @DynamicPropertySource
    static void configurePostgreSql(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRESQL::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
    }

    @Test
    @DisplayName("Должен сохранить и загрузить заказ через PostgreSQL")
    void shouldSaveAndLoadOrder() {
        Order order = order();

        adapter.save(order);
        Optional<Order> result = adapter.load(order.id());

        assertThat(result).isPresent();
        assertThat(result.orElseThrow().id()).isEqualTo(order.id());
        assertThat(result.orElseThrow().customerId()).isEqualTo(order.customerId());
        assertThat(result.orElseThrow().lines()).hasSize(1);
        assertThat(repository.findById(order.id().value())).isPresent();
    }

    @Test
    @DisplayName("Должен обновить сохраненный заказ")
    void shouldUpdateSavedOrder() {
        Order order = order();
        adapter.save(order);
        order.cancel();

        adapter.save(order);
        Order result = adapter.load(order.id()).orElseThrow();

        assertThat(result.status()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(repository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Должен вернуть пустой результат для отсутствующего заказа")
    void shouldReturnEmptyForMissingOrder() {
        Optional<Order> result = adapter.load(OrderId.newId());

        assertThat(result).isEmpty();
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
                        new Money(new BigDecimal("10.00"), Currency.getInstance("EUR"))
                ))
        );
    }
}
