package ru.example.expr;

/**
 * Бинарные операции языка выражений.
 *
 * <p>Каждая константа перечисления хранит приоритет, ассоциативность
 * и реализует вычисление в теле константы.</p>
 */
public enum BinaryOperator {

    /** Сложение. */
    ADD(1, Associativity.LEFT) {
        @Override
        public long apply(long left, long right) {
            return left + right;
        }
    },

    /** Вычитание. */
    SUBTRACT(1, Associativity.LEFT) {
        @Override
        public long apply(long left, long right) {
            return left - right;
        }
    },

    /** Умножение. */
    MULTIPLY(2, Associativity.LEFT) {
        @Override
        public long apply(long left, long right) {
            return left * right;
        }
    },

    /** Деление. */
    DIVIDE(2, Associativity.LEFT) {
        @Override
        public long apply(long left, long right) {
            if (right == 0) {
                throw new DivisionByZeroException("Деление на ноль при вычислении выражения");
            }
            return left / right;
        }
    },

    /** Возведение в степень. */
    POWER(4, Associativity.RIGHT) {
        @Override
        public long apply(long left, long right) {
            if (right < 0) {
                throw new EvaluationException(
                        "Отрицательный показатель степени не поддерживается для целочисленного возведения в степень"
                );
            }

            long result = 1L;
            long base = left;
            long exponent = right;

            while (exponent > 0) {
                if ((exponent & 1L) != 0) {
                    result *= base;
                }

                exponent >>= 1;

                if (exponent > 0) {
                    base *= base;
                }
            }

            return result;
        }
    };

    private final int precedence;
    private final Associativity associativity;

    BinaryOperator(int precedence, Associativity associativity) {
        this.precedence = precedence;
        this.associativity = associativity;
    }

    /**
     * Возвращает приоритет операции.
     *
     * @return приоритет операции
     */
    public int precedence() {
        return precedence;
    }

    /**
     * Возвращает ассоциативность операции.
     *
     * @return ассоциативность операции
     */
    public Associativity associativity() {
        return associativity;
    }

    /**
     * Вычисляет результат бинарной операции.
     *
     * @param left  левый операнд
     * @param right правый операнд
     * @return результат операции
     */
    public abstract long apply(long left, long right);
}