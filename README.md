# Order Management

Order Management - учебный сервис управления заказами, построенный на Java 25 и Spring Boot 4.0.6 в стиле Hexagonal Architecture + DDD.

Проект показывает, как отделить доменную модель от инфраструктуры: бизнес-правила живут в `domain`, сценарии приложения - в `application`, а REST, JPA, PostgreSQL, Flyway и внешние адаптеры - в `infrastructure`.

## Что делает проект

Сервис управляет жизненным циклом заказа:

- создает заказ с клиентом и строками товаров;
- проверяет доменные инварианты заказа;
- резервирует складские остатки через outbound port;
- запускает оплату через outbound port;
- сохраняет заказ в PostgreSQL через Spring Data JPA;
- публикует доменные события через outbound port;
- возвращает заказ по идентификатору;
- отменяет заказ, если это разрешено доменными правилами.

В текущей версии внешние платежная и складская системы представлены fake-адаптерами. Они реализуют порты приложения и позволяют запускать REST-сценарии без настоящих внешних интеграций.

## Технологии

- Java 25
- Spring Boot 4.0.6
- Gradle
- Spring Web MVC
- Spring Data JPA
- PostgreSQL
- Flyway
- Jakarta Validation
- JUnit 5
- Mockito
- AssertJ
- Testcontainers
- PostgreSQL Testcontainer
- JaCoCo 0.8.14

Lombok не используется. В коде применяются `jakarta.*` API.

## Архитектура

Проект разделен на bounded context `order`:

```text
src/main/java/dev/tschumin/ordermanagement/order
+-- domain
+-- application
+-- infrastructure
```

### Domain Layer

Пакет:

```text
dev.tschumin.ordermanagement.order.domain
```

Содержит чистую доменную модель без Spring, JPA и REST:

- `Order` - aggregate root заказа;
- `OrderLine` - строка заказа;
- `OrderId`, `CustomerId`, `ProductId` - идентификаторы;
- `Money`, `Quantity` - value objects;
- `OrderStatus` - статусы заказа;
- `DomainEvent`, `OrderPlacedEvent` - доменные события;
- `OrderDomainException` - доменное исключение.

`Order` не является анемичной моделью. Все изменения состояния выполняются через методы агрегата:

- `create(...)`
- `restore(...)`
- `reserveInventory()`
- `markPaymentStarted()`
- `markPaid()`
- `failPayment()`
- `cancel()`
- `totalPrice()`

Доменная модель проверяет инварианты:

- заказ не может быть пустым;
- цена должна быть больше нуля;
- количество должно быть больше нуля;
- запрещены некорректные переходы статусов;
- оплаченный заказ нельзя отменить;
- повторное резервирование запрещено;
- нельзя складывать денежные суммы в разных валютах.

### Application Layer

Пакет:

```text
dev.tschumin.ordermanagement.order.application
```

Содержит use cases, commands и ports. Этот слой зависит от доменной модели и абстракций портов, но не зависит от Spring MVC, JPA или REST DTO.

Inbound ports:

- `PlaceOrderUseCase`
- `GetOrderUseCase`
- `CancelOrderUseCase`

Handlers:

- `PlaceOrderHandler`
- `GetOrderHandler`
- `CancelOrderHandler`

Commands:

- `PlaceOrderCommand`
- `PlaceOrderLineCommand`
- `CancelOrderCommand`

Outbound ports:

- `SaveOrderPort`
- `LoadOrderPort`
- `InventoryPort`
- `PaymentPort`
- `DomainEventPublisherPort`

Основной сценарий создания заказа:

1. REST adapter принимает HTTP-запрос и преобразует его в `PlaceOrderCommand`.
2. `PlaceOrderHandler` создает агрегат `Order`.
3. Handler вызывает `InventoryPort`.
4. Агрегат переводится в состояние `INVENTORY_RESERVED`.
5. Handler вызывает `PaymentPort`.
6. Агрегат переводится в состояние `PAYMENT_STARTED`.
7. Handler сохраняет заказ через `SaveOrderPort`.
8. Handler публикует доменные события через `DomainEventPublisherPort`.

Бизнес-правила не дублируются в application layer: переходы статусов и инварианты остаются внутри `Order`.

### Infrastructure Layer

Пакет:

```text
dev.tschumin.ordermanagement.order.infrastructure
```

Содержит адаптеры и конфигурацию Spring:

- REST adapter;
- persistence adapter;
- fake payment adapter;
- fake inventory adapter;
- domain event publisher adapter;
- Spring configuration для wiring use case handlers.

## REST API

Base path:

```text
/api/v1/orders
```

### Создать заказ

```http
POST /api/v1/orders
Content-Type: application/json
```

Пример запроса:

```json
{
  "customerId": "00000000-0000-0000-0000-000000000001",
  "lines": [
    {
      "productId": "00000000-0000-0000-0000-000000000002",
      "quantity": 2,
      "unitPriceAmount": 10.00,
      "unitPriceCurrency": "EUR"
    }
  ]
}
```

Успешный ответ: `201 Created`.

Пример ответа:

```json
{
  "id": "generated-order-uuid",
  "customerId": "00000000-0000-0000-0000-000000000001",
  "status": "PAYMENT_STARTED",
  "inventoryReserved": true,
  "lines": [
    {
      "productId": "00000000-0000-0000-0000-000000000002",
      "quantity": 2,
      "unitPriceAmount": 10.00,
      "unitPriceCurrency": "EUR",
      "totalAmount": 20.00,
      "totalCurrency": "EUR"
    }
  ],
  "totalAmount": 20.00,
  "totalCurrency": "EUR"
}
```

### Получить заказ

```http
GET /api/v1/orders/{orderId}
```

Успешный ответ: `200 OK`.

Если заказ не найден, возвращается `404 Not Found`.

### Отменить заказ

```http
POST /api/v1/orders/{orderId}/cancel
```

Успешный ответ: `204 No Content`.

Если заказ оплачен или уже отменен, доменная модель выбрасывает ошибку, а REST adapter возвращает ошибочный ответ.

## Ошибки REST API

REST adapter возвращает `ErrorResponse`:

```json
{
  "timestamp": "2026-06-02T00:00:00Z",
  "status": 400,
  "error": "Ошибка валидации",
  "message": "Некорректный REST-запрос",
  "path": "/api/v1/orders",
  "details": [
    "lines: Заказ должен содержать хотя бы одну строку"
  ]
}
```

Типовые ошибки:

- `400 Bad Request` - ошибка формата UUID, валюты или validation;
- `404 Not Found` - заказ не найден;
- `409 Conflict` - нарушено доменное правило.

## Persistence

Persistence adapter находится в пакете:

```text
dev.tschumin.ordermanagement.order.infrastructure.adapter.out.persistence
```

Состав:

- `OrderJpaEntity`
- `OrderLineJpaEntity`
- `SpringDataOrderJpaRepository`
- `OrderPersistenceMapper`
- `OrderPersistenceAdapter`

Domain model и JPA entities являются разными объектами. Преобразование выполняется явно через `OrderPersistenceMapper`.

В JPA-модели используются:

- UUID identifiers;
- `@Version` для optimistic locking;
- audit timestamps `created_at` и `updated_at`;
- связь `orders` -> `order_lines`;
- cascade и orphan removal для строк заказа.

Flyway migration:

```text
src/main/resources/db/migration/V1__create_orders_tables.sql
```

Migration создает:

- таблицу `orders`;
- таблицу `order_lines`;
- primary keys;
- foreign key `order_lines.order_id -> orders.id`;
- индексы;
- optimistic locking columns;
- audit timestamp columns.

## База данных

В production/runtime используется PostgreSQL.

В репозитории есть `compose.yaml` для локального PostgreSQL, а интеграционные persistence-тесты используют PostgreSQL Testcontainer.

Перед запуском приложения убедитесь, что PostgreSQL доступен и параметры подключения заданы через Spring Boot configuration. В текущем `application.properties` задано только имя приложения:

```properties
spring.application.name=order-management
```

Параметры datasource можно передать через environment variables или локальный профиль, например:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/order_management
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.hibernate.ddl-auto=validate
```

Схема создается Flyway, поэтому для JPA рекомендуется `ddl-auto=validate`.

## Тестирование

Тесты покрывают:

- доменную модель и value objects;
- application handlers;
- persistence mapper;
- persistence adapter с PostgreSQL Testcontainer;
- REST controller;
- fake payment gateway adapter;
- fake inventory adapter;
- загрузку Spring Boot context.

Каждый тестовый метод использует `@DisplayName` на русском языке. Каждый тестовый класс начинается с JavaDoc со списком проверяемых сценариев.

Запуск всех тестов:

```powershell
.\gradlew.bat clean test
```

Полная проверка проекта:

```powershell
.\gradlew.bat check
```

## JaCoCo

JaCoCo настроен в Gradle:

- tool version: `0.8.14`;
- agent включен на задаче `test`;
- HTML и XML report формируются после тестов.

Получить отчет покрытия:

```powershell
.\gradlew.bat clean test
```

HTML-отчет:

```text
build/reports/jacoco/test/html/index.html
```

XML-отчет:

```text
build/reports/jacoco/test/jacocoTestReport.xml
```

Минимальный процент покрытия намеренно не задан. При этом в текущем состоянии проекта все production-классы имеют покрытие строками.

## Запуск приложения

Сборка:

```powershell
.\gradlew.bat clean build
```

Запуск:

```powershell
.\gradlew.bat bootRun
```

Для полноценного запуска REST API нужен доступный PostgreSQL и корректная datasource-конфигурация.

Для проверки distributed tracing через локальный port-forward Jaeger collector можно
добавить переменные окружения:

```powershell
$env:OPENTRACING_JAEGER_ENABLED = "true"
$env:OPENTRACING_JAEGER_SERVICE_NAME = "order-management"
$env:OPENTRACING_JAEGER_HTTP_SENDER_URL = "http://localhost:14268/api/traces"
```

Jaeger UI при port-forward доступен по адресу:

```text
http://localhost:16686
```

## Структура проекта

```text
src
+-- main
|   +-- java
|   |   +-- dev/tschumin/ordermanagement
|   |       +-- OrderManagementApplication.java
|   |       +-- order
|   |           +-- domain
|   |           +-- application
|   |           +-- infrastructure
|   +-- resources
|       +-- application.properties
|       +-- db/migration/V1__create_orders_tables.sql
+-- test
    +-- java
        +-- dev/tschumin/ordermanagement
```

## Архитектурные правила проекта

- Domain не зависит от Spring, JPA, REST и инфраструктуры.
- Application layer не зависит от REST DTO и JPA entities.
- REST DTO не попадают в domain и application.
- JPA entities не используются как domain model.
- Business rules находятся в aggregate root и value objects.
- Infrastructure зависит от application ports и domain model.
- Lombok не используется.
- Все новые классы, методы, private fields и private methods должны иметь JavaDoc на русском языке.
