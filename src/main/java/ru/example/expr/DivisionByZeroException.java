package ru.example.expr;

/**
 * Ошибка деления на ноль.
 */
public class DivisionByZeroException extends EvaluationException {

    /**
     * Создаёт ошибку деления на ноль.
     *
     * @param message сообщение об ошибке
     */
    public DivisionByZeroException(String message) {
        super(message);
    }
}