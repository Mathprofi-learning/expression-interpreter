package ru.example.expr;

/**
 * Унарные операции языка выражений.
 */
public enum UnaryOperator {

    /** Унарный плюс. */
    PLUS {
        @Override
        public long apply(long operand) {
            return operand;
        }
    },

    /** Унарный минус. */
    NEGATE {
        @Override
        public long apply(long operand) {
            if (operand == Long.MIN_VALUE) {
                throw new EvaluationException("Переполнение при отрицании числа");
            }
            return -operand;
        }
    };

    /**
     * Вычисляет результат унарной операции.
     *
     * @param operand операнд
     * @return результат операции
     */
    public abstract long apply(long operand);
}