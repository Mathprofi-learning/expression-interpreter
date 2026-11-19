package ru.example.expr;

import java.util.Objects;

/**
 * Переменная в выражении.
 *
 * @param name имя переменной
 */
public record VariableNode(String name) implements Expr {

    /**
     * Компактная проверка имени переменной.
     */
    public VariableNode {
        Objects.requireNonNull(name, "Имя переменной не может быть null");

        if (name.isBlank()) {
            throw new IllegalArgumentException("Имя переменной не может быть пустым");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long evaluate(EvaluationContext context) {
        return context.value(name);
    }
}