package ru.example.expr;

/**
 * Числовой литерал.
 *
 * @param value целочисленное значение
 */
public record NumberNode(long value) implements Expr {

    /**
     * {@inheritDoc}
     */
    @Override
    public long evaluate(EvaluationContext context) {
        return value;
    }
}