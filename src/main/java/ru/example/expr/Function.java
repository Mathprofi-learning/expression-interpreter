package ru.example.expr;

import java.util.Optional;

/**
 * Функции языка выражений.
 */
public enum Function {

    /** Функция минимума из двух аргументов. */
    MIN("min", 2) {
        @Override
        public long apply(long[] args) {
            return Math.min(args[0], args[1]);
        }
    },

    /** Функция максимума из двух аргументов. */
    MAX("max", 2) {
        @Override
        public long apply(long[] args) {
            return Math.max(args[0], args[1]);
        }
    },

    /** Функция модуля числа. */
    ABS("abs", 1) {
        @Override
        public long apply(long[] args) {
            long value = args[0];

            if (value == Long.MIN_VALUE) {
                throw new EvaluationException("Модуль Long.MIN_VALUE не представим в типе long");
            }

            return Math.abs(value);
        }
    };

    private final String functionName;
    private final int arity;

    Function(String functionName, int arity) {
        this.functionName = functionName;
        this.arity = arity;
    }

    /**
     * Возвращает имя функции.
     *
     * @return имя функции
     */
    public String functionName() {
        return functionName;
    }

    /**
     * Возвращает количество аргументов функции.
     *
     * @return количество аргументов
     */
    public int arity() {
        return arity;
    }

    /**
     * Ищет функцию по имени.
     *
     * @param name имя функции
     * @return функция, если найдена, иначе пустой {@link Optional}
     */
    public static Optional<Function> byName(String name) {
        for (var function : values()) {
            if (function.functionName.equals(name)) {
                return Optional.of(function);
            }
        }

        return Optional.empty();
    }

    /**
     * Вычисляет результат функции.
     *
     * @param args массив уже вычисленных аргументов
     * @return результат функции
     */
    public abstract long apply(long[] args);
}