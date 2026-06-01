package dev.tschumin.ordermanagement;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Тесты запуска Spring Boot приложения.
 *
 * <ul>
 *     <li>Проверяет успешную загрузку Spring application context.</li>
 * </ul>
 */
@SpringBootTest
class OrderManagementApplicationTests {

    @Test
    @DisplayName("Должен загрузить контекст Spring Boot приложения")
    void contextLoads() {
    }

}
