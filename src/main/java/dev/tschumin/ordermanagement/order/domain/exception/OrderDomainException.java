package dev.tschumin.ordermanagement.order.domain.exception;

/**
 * Базовое доменное исключение контекста заказов.
 */
public class OrderDomainException extends RuntimeException {

    /**
     * Создает доменное исключение с сообщением.
     *
     * @param message описание нарушения доменного правила
     */
    public OrderDomainException(String message) {
        super(message);
    }

    /**
     * Создает доменное исключение с сообщением и исходной причиной.
     *
     * @param message описание нарушения доменного правила
     * @param cause исходная причина исключения
     */
    public OrderDomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
