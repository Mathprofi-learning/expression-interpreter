package ru.example.expr;

/**
 * Базовый интерфейс узла дерева арифметического выражения.
 *
 * <p>Иерархия закрытая, чтобы набор узлов модели был фиксированным.</p>
 */
public sealed interface Expr permits
        NumberNode,
        VariableNode,
        UnaryOpNode,
        BinaryOpNode,
        FunctionCallNode {

    /**
     * Вычисляет текущее выражение.
     *
     * @param context контекст переменных
     * @return результат вычисления
     */
    long evaluate(EvaluationContext context);
}