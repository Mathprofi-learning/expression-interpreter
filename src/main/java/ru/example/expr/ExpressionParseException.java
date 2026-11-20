package ru.example.expr;

/**
 * Ошибка разбора выражения.
 *
 * <p>Содержит позицию в строке, где обнаружена ошибка.</p>
 */
public class ExpressionParseException extends RuntimeException {

    private final int position;

    /**
     * Создаёт исключение разбора выражения.
     *
     * @param message  сообщение об ошибке
     * @param position позиция в строке, начиная с 1
     */
    public ExpressionParseException(String message, int position) {
        super(message + " (позиция: " + position + ")");
        this.position = position;
    }

    /**
     * Возвращает позицию ошибки в строке.
     *
     * @return позиция ошибки, начиная с 1
     */
    public int getPosition() {
        return position;
    }
}