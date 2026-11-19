package ru.example.expr;

import java.util.List;
import java.util.Objects;

/**
 * Узел вызова функции.
 *
 * @param function  функция
 * @param arguments список аргументов; при создании копируется
 */
public record FunctionCallNode(Function function, List<Expr> arguments) implements Expr {

    /**
     * Компактная проверка и копирование аргументов.
     */
    public FunctionCallNode {
        Objects.requireNonNull(function, "Функция не может быть null");
        Objects.requireNonNull(arguments, "Список аргументов не может быть null");

        arguments = List.copyOf(arguments);

        if (arguments.size() != function.arity()) {
            throw new IllegalArgumentException(
                    "Функция " + function.functionName() + " требует " + function.arity()
                            + " аргументов, но передано " + arguments.size()
            );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long evaluate(EvaluationContext context) {
        var values = new long[arguments.size()];

        for (var i = 0; i < values.length; i++) {
            values[i] = arguments.get(i).evaluate(context);
        }

        return function.apply(values);
    }
}