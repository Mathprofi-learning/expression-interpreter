package ru.example.expr;

/**
 * Базовая ошибка вычисления выражения.
 */
public class EvaluationException extends RuntimeException {

    /**
     * Создаёт ошибку вычисления.
     *
     * @param message сообщение об ошибке
     */
    public EvaluationException(String message) {
        super(message);
    }

    /**
     * Создаёт ошибку вычисления с причиной.
     *
     * @param message сообщение об ошибке
     * @param cause   причина
     */
    public EvaluationException(String message, Throwable cause) {
        super(message, cause);
    }
}