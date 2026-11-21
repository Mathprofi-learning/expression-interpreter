package ru.example.expr;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Тесты интерпретатора выражений и модели.
 */
class ExpressionParserTest {

    private final ExpressionParser parser = new ExpressionParser();

    private final EvaluationContext context = new EvaluationContext(Map.of(
            "x", 10L,
            "y", -3L,
            "a", 7L,
            "b", 2L
    ));

    private long eval(String input) {
        return parser.parse(input).evaluate(context);
    }

    @Test
    void additionWorks() {
        assertEquals(3L, eval("1 + 2"));
    }

    @Test
    void subtractionWorks() {
        assertEquals(2L, eval("5 - 3"));
    }

    @Test
    void multiplicationWorks() {
        assertEquals(6L, eval("2 * 3"));
    }

    @Test
    void divisionWorks() {
        assertEquals(5L, eval("10 / 2"));
    }

    @Test
    void multiplicationHasHigherPrecedence() {
        assertEquals(6L, eval("2 + 2 * 2"));
    }

    @Test
    void parenthesesChangePrecedence() {
        assertEquals(8L, eval("(2 + 2) * 2"));
    }

    @Test
    void unaryMinusWorks() {
        assertEquals(-5L, eval("-5"));
        assertEquals(5L, eval("--5"));
        assertEquals(5L, eval("+5"));
    }

    @Test
    void powerIsRightAssociative() {
        assertEquals(512L, eval("2 ^ 3 ^ 2"));
    }

    @Test
    void unaryMinusHasLowerPrecedenceThanPower() {
        assertEquals(-4L, eval("-2 ^ 2"));
    }

    @Test
    void functionsWork() {
        assertEquals(3L, eval("min(3, 4)"));
        assertEquals(4L, eval("max(3, 4)"));
        assertEquals(8L, eval("abs(-8)"));
        assertEquals(10L, eval("max(abs(y), x)"));
    }

    @Test
    void variablesFromContextWork() {
        assertEquals(4L, eval("x + y * 2"));
        assertEquals(-3L, eval("min(x, y)"));
    }

    @Test
    void divisionByZeroThrows() {
        assertThrows(DivisionByZeroException.class, () -> eval("1 / 0"));
    }

    @Test
    void unknownVariableThrows() {
        assertThrows(UnknownVariableException.class, () -> eval("z + 1"));
    }

    @Test
    void unknownVariableExceptionContainsName() {
        var ex = assertThrows(UnknownVariableException.class, () -> eval("z + 1"));
        assertEquals("z", ex.getVariableName());
    }

    @Test
    void syntaxErrorContainsPosition() {
        var ex = assertThrows(ExpressionParseException.class, () -> parser.parse("2 + * 3"));

        assertEquals(5, ex.getPosition());
        assertTrue(ex.getMessage().contains("позиция"));
    }

    @Test
    void unknownFunctionIsParseError() {
        assertThrows(ExpressionParseException.class, () -> parser.parse("foo(1)"));
    }

    @Test
    void wrongFunctionArityIsParseError() {
        assertThrows(ExpressionParseException.class, () -> parser.parse("min(1)"));
    }

    @Test
    void negativeExponentThrowsEvaluationException() {
        assertThrows(EvaluationException.class, () -> eval("2 ^ -1"));
    }

    @Test
    void boundaryLongMaxValueIsParsed() {
        assertEquals(Long.MAX_VALUE, eval("9223372036854775807"));
    }

    @Test
    void tooBigNumberIsParseError() {
        assertThrows(ExpressionParseException.class, () -> parser.parse("9223372036854775808"));
    }

    @Test
    void emptyInputIsParseError() {
        assertThrows(ExpressionParseException.class, () -> parser.parse("   "));
    }

    @Test
    void numberNodeEqualsAndHashCodeContract() {
        var first = new NumberNode(5L);
        var second = new NumberNode(5L);

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertNotEquals(first, new NumberNode(6L));
    }

    @Test
    void variableNodeEqualsAndHashCodeContract() {
        var first = new VariableNode("x");
        var second = new VariableNode("x");

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertNotEquals(first, new VariableNode("y"));
    }

    @Test
    void evaluationContextCopiesInputMap() {
        var map = new HashMap<String, Long>();
        map.put("a", 1L);

        var ctx = new EvaluationContext(map);
        map.put("b", 2L);

        assertFalse(ctx.containsVariable("b"));
    }

    @Test
    void withVariableCreatesNewContext() {
        var original = EvaluationContext.empty();
        var extended = original.withVariable("z", 42L);

        assertFalse(original.containsVariable("z"));
        assertEquals(42L, extended.value("z"));
    }

    @Test
    void functionNodeCopiesArguments() {
        var args = new ArrayList<Expr>();
        args.add(new NumberNode(1L));
        args.add(new NumberNode(2L));

        var node = new FunctionCallNode(Function.MIN, args);
        args.clear();

        assertEquals(2, node.arguments().size());
        assertThrows(UnsupportedOperationException.class, () -> node.arguments().clear());
    }

    @Test
    void functionNodeRejectsWrongArity() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new FunctionCallNode(Function.ABS, List.of(new NumberNode(1L), new NumberNode(2L)))
        );
    }

    @Test
    void binaryOperatorMetadataIsCorrect() {
        assertTrue(BinaryOperator.POWER.precedence() > BinaryOperator.MULTIPLY.precedence());
        assertEquals(Associativity.RIGHT, BinaryOperator.POWER.associativity());
        assertEquals(Associativity.LEFT, BinaryOperator.ADD.associativity());
    }

    @Test
    void functionByNameWorks() {
        assertTrue(Function.byName("min").isPresent());
        assertTrue(Function.byName("unknown").isEmpty());
    }
}