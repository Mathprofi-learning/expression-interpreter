package ru.example.expr;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Контекст вычисления выражения.
 *
 * <p>Хранит значения переменных. Внутренний словарь копируется,
 * поэтому изменяемое состояние наружу не утекает.</p>
 */
public final class EvaluationContext {

    private final Map<String, Long> variables;

    /**
     * Создаёт контекст из словаря переменных.
     *
     * @param variables словарь переменных; копируется
     */
    public EvaluationContext(Map<String, Long> variables) {
        Objects.requireNonNull(variables, "Словарь переменных не может быть null");
        this.variables = Map.copyOf(variables);
    }

    /**
     * Создаёт пустой контекст.
     *
     * @return пустой контекст
     */
    public static EvaluationContext empty() {
        return new EvaluationContext(Map.of());
    }

    /**
     * Возвращает новый контекст с добавленной или изменённой переменной.
     *
     * @param name  имя переменной
     * @param value значение переменной
     * @return новый контекст
     */
    public EvaluationContext withVariable(String name, long value) {
        Objects.requireNonNull(name, "Имя переменной не может быть null");

        var copy = new HashMap<>(variables);
        copy.put(name, value);

        return new EvaluationContext(copy);
    }

    /**
     * Возвращает значение переменной.
     *
     * @param name имя переменной
     * @return значение переменной
     * @throws UnknownVariableException если переменная не найдена
     */
    public long value(String name) {
        var value = variables.get(name);

        if (value == null) {
            throw new UnknownVariableException(name);
        }

        return value;
    }

    /**
     * Проверяет наличие переменной.
     *
     * @param name имя переменной
     * @return {@code true}, если переменная есть в контексте
     */
    public boolean containsVariable(String name) {
        return variables.containsKey(name);
    }
}