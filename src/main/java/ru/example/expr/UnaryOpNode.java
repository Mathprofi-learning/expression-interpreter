package ru.example.expr;

import java.util.Objects;

/**
 * Узел унарной операции.
 *
 * @param operator унарная операция
 * @param operand  операнд
 */
public record UnaryOpNode(UnaryOperator operator, Expr operand) implements Expr {

    /**
     * Компактная проверка полей узла.
     */
    public UnaryOpNode {
        Objects.requireNonNull(operator, "Оператор не может быть null");
        Objects.requireNonNull(operand, "Операнд не может быть null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long evaluate(EvaluationContext context) {
        return operator.apply(operand.evaluate(context));
    }
}