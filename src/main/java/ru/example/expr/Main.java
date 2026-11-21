package ru.example.expr;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Консольная демонстрация работы интерпретатора выражений.
 */
public final class Main {

    private Main() {
    }

    /**
     * Точка входа в консольное приложение.
     *
     * @param args аргументы командной строки не используются
     */
    public static void main(String[] args) {
        var parser = new ExpressionParser();

        var context = new EvaluationContext(Map.of(
                "x", 10L,
                "y", -3L,
                "a", 7L,
                "b", 2L
        ));

        System.out.println("Модель: интерпретатор арифметических выражений");
        System.out.println("Контекст: x = 10, y = -3, a = 7, b = 2");
        System.out.println();

        var sources = List.of(
                "1 + 2",
                "5 - 3",
                "2 * 3",
                "10 / 2",
                "2 + 2 * 2",
                "(2 + 2) * 2",
                "100 - (30 + 5)",
                "-5",
                "+5",
                "--5",
                "2 ^ 3",
                "2 ^ 3 ^ 2",
                "-2 ^ 2",
                "2 ^ (1 + 1)",
                "min(3, 4)",
                "max(3, 4)",
                "abs(-8)",
                "min(x, y)",
                "max(abs(y), x)",
                "x + y * 2",
                "(x - y) / 2",
                "a ^ b + 1",
                "min(a, b) * max(a, b)",
                "abs(min(y, 0))",
                "x / 3",
                "((1 + 2) * (3 + 4))",
                "2 ^ (3 - 1)",
                "max(1, min(2, 3))",
                "abs(-(x + y))",
                "min(2 + 3, 4 * 2)",
                "max(-5, -10)",
                "-(3 * 4) + 2"
        );

        var expressions = new ArrayList<Expr>();

        for (var source : sources) {
            expressions.add(parser.parse(source));
        }

        var totalNodes = expressions.stream()
                .mapToInt(Main::countNodes)
                .sum();

        System.out.println("Объектов верхнего уровня: " + expressions.size());
        System.out.println("Всего узлов модели: " + totalNodes);
        System.out.println();

        System.out.println("1. Базовые операции: разбор и вычисление");
        System.out.println("----------------------------------------");

        for (var i = 0; i < sources.size(); i++) {
            var source = sources.get(i);
            var result = expressions.get(i).evaluate(context);

            System.out.printf("%-35s = %d%n", source, result);
        }

        System.out.println();
        System.out.println("2. Обработка исключений");
        System.out.println("-----------------------");

        demonstrateError(parser, context, "1 / 0", "Деление на ноль");
        demonstrateError(parser, context, "z + 1", "Неизвестная переменная");
        demonstrateError(parser, context, "2 + * 3", "Синтаксическая ошибка");
        demonstrateError(parser, context, "foo(1)", "Неизвестная функция");
        demonstrateError(parser, context, "min(1)", "Неверное число аргументов");
        demonstrateError(parser, context, "2 ^ -1", "Отрицательный показатель степени");

        System.out.println("3. Демонстрация switch с сопоставлением по образцу");
        System.out.println("--------------------------------------------------");

        for (var i = 0; i < 6 && i < expressions.size(); i++) {
            System.out.println(describe(expressions.get(i)));
        }
    }

    private static void demonstrateError(
            ExpressionParser parser,
            EvaluationContext context,
            String source,
            String caption
    ) {
        System.out.println(caption + ": " + source);

        try {
            var expression = parser.parse(source);
            var result = expression.evaluate(context);
            System.out.println("  Результат: " + result);
        } catch (ExpressionParseException | EvaluationException ex) {
            System.out.println("  " + ex.getClass().getSimpleName() + ": " + ex.getMessage());
        }

        System.out.println();
    }

    private static int countNodes(Expr expr) {
        return 1 + switch (expr) {
            case NumberNode n -> 0;
            case VariableNode v -> 0;
            case UnaryOpNode u -> countNodes(u.operand());
            case BinaryOpNode b -> countNodes(b.left()) + countNodes(b.right());
            case FunctionCallNode f -> f.arguments().stream()
                    .mapToInt(Main::countNodes)
                    .sum();
        };
    }

    private static String describe(Expr expr) {
        return switch (expr) {
            case NumberNode n -> "Число: " + n.value();
            case VariableNode v -> "Переменная: " + v.name();
            case UnaryOpNode u -> "Унарная операция: " + u.operator();
            case BinaryOpNode b -> "Бинарная операция: " + b.operator();
            case FunctionCallNode f -> "Функция: " + f.function().functionName();
        };
    }
}