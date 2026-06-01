package dev.tschumin.ordermanagement.order.infrastructure.adapter.in.rest;

import java.time.Instant;
import java.util.List;

/**
 * REST-ответ с описанием ошибки.
 */
public class ErrorResponse {

    /**
     * Момент формирования ответа.
     */
    private final Instant timestamp;

    /**
     * HTTP-статус ответа.
     */
    private final int status;

    /**
     * Краткое имя ошибки.
     */
    private final String error;

    /**
     * Сообщение об ошибке.
     */
    private final String message;

    /**
     * Путь HTTP-запроса.
     */
    private final String path;

    /**
     * Детали ошибки.
     */
    private final List<String> details;

    /**
     * Создает REST-ответ с описанием ошибки.
     *
     * @param timestamp момент формирования ответа
     * @param status HTTP-статус ответа
     * @param error краткое имя ошибки
     * @param message сообщение об ошибке
     * @param path путь HTTP-запроса
     * @param details детали ошибки
     */
    public ErrorResponse(Instant timestamp, int status, String error, String message, String path, List<String> details) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.details = List.copyOf(details);
    }

    /**
     * Возвращает момент формирования ответа.
     *
     * @return момент формирования ответа
     */
    public Instant getTimestamp() {
        return timestamp;
    }

    /**
     * Возвращает HTTP-статус ответа.
     *
     * @return HTTP-статус ответа
     */
    public int getStatus() {
        return status;
    }

    /**
     * Возвращает краткое имя ошибки.
     *
     * @return краткое имя ошибки
     */
    public String getError() {
        return error;
    }

    /**
     * Возвращает сообщение об ошибке.
     *
     * @return сообщение об ошибке
     */
    public String getMessage() {
        return message;
    }

    /**
     * Возвращает путь HTTP-запроса.
     *
     * @return путь HTTP-запроса
     */
    public String getPath() {
        return path;
    }

    /**
     * Возвращает детали ошибки.
     *
     * @return детали ошибки
     */
    public List<String> getDetails() {
        return details;
    }
}
