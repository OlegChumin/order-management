package dev.tschumin.ordermanagement.order.infrastructure.adapter.in.rest;

import dev.tschumin.ordermanagement.order.application.exception.OrderNotFoundException;
import dev.tschumin.ordermanagement.order.domain.exception.OrderDomainException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;
import java.util.List;

/**
 * Обработчик исключений REST-адаптера заказов.
 */
@RestControllerAdvice(assignableTypes = OrderController.class)
public class OrderRestExceptionHandler {

    /**
     * Обрабатывает ошибки валидации REST-запросов.
     *
     * @param exception исключение валидации аргумента метода
     * @param request HTTP-запрос
     * @return REST-ответ с ошибкой валидации
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        List<String> details = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();
        return build(HttpStatus.BAD_REQUEST, "Ошибка валидации", "Некорректный REST-запрос", request, details);
    }

    /**
     * Обрабатывает ошибки отсутствия заказа.
     *
     * @param exception исключение отсутствующего заказа
     * @param request HTTP-запрос
     * @return REST-ответ с ошибкой отсутствия ресурса
     */
    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(OrderNotFoundException exception, HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, "Заказ не найден", exception.getMessage(), request, List.of());
    }

    /**
     * Обрабатывает ошибки доменных правил.
     *
     * @param exception доменное исключение
     * @param request HTTP-запрос
     * @return REST-ответ с ошибкой бизнес-валидации
     */
    @ExceptionHandler(OrderDomainException.class)
    public ResponseEntity<ErrorResponse> handleDomain(OrderDomainException exception, HttpServletRequest request) {
        return build(HttpStatus.CONFLICT, "Ошибка бизнес-правила", exception.getMessage(), request, List.of());
    }

    /**
     * Обрабатывает ошибки некорректного формата аргументов REST-запроса.
     *
     * @param exception исключение преобразования аргумента метода
     * @param request HTTP-запрос
     * @return REST-ответ с ошибкой формата аргумента
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException exception,
            HttpServletRequest request
    ) {
        return build(
                HttpStatus.BAD_REQUEST,
                "Ошибка формата",
                "Некорректное значение параметра: " + exception.getName(),
                request,
                List.of()
        );
    }

    /**
     * Обрабатывает ошибки преобразования REST DTO в доменные значения.
     *
     * @param exception исключение некорректного аргумента
     * @param request HTTP-запрос
     * @return REST-ответ с ошибкой аргумента
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException exception,
            HttpServletRequest request
    ) {
        return build(HttpStatus.BAD_REQUEST, "Некорректный аргумент", exception.getMessage(), request, List.of());
    }

    /**
     * Создает REST-ответ с ошибкой.
     *
     * @param status HTTP-статус ответа
     * @param error краткое имя ошибки
     * @param message сообщение об ошибке
     * @param request HTTP-запрос
     * @param details детали ошибки
     * @return REST-ответ с ошибкой
     */
    private ResponseEntity<ErrorResponse> build(
            HttpStatus status,
            String error,
            String message,
            HttpServletRequest request,
            List<String> details
    ) {
        ErrorResponse response = new ErrorResponse(
                Instant.now(),
                status.value(),
                error,
                message,
                request.getRequestURI(),
                details
        );
        return ResponseEntity.status(status).body(response);
    }
}
