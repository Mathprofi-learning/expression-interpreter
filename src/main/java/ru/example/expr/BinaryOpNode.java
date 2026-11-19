package ru.example.expr;

import java.util.Objects;

/**
 * Узел бинарной операции.
 *
 * @param operator бинарная операция
 * @param left     левый операнд
 * @param right    правый операнд
 */
public record BinaryOpNode(BinaryOperator operator, Expr left, Expr right) implements Expr {

    /**
     * Компактная проверка полей узла.
     */
    public BinaryOpNode {
        Objects.requireNonNull(operator, "Оператор не может быть null");
        Objects.requireNonNull(left, "Левый операнд не может быть null");
        Objects.requireNonNull(right, "Правый операнд не может быть null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long evaluate(EvaluationContext context) {
        long leftValue = left.evaluate(context);
        long rightValue = right.evaluate(context);

        return operator.apply(leftValue, rightValue);
    }
}