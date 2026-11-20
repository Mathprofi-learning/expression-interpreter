package ru.example.expr;

import java.util.ArrayList;
import java.util.List;

/**
 * Парсер арифметических выражений на основе рекурсивного спуска.
 *
 * <p>Поддерживаются числа, переменные, скобки, бинарные операции,
 * унарный плюс и минус, правоассоциированная степень, функции
 * {@code min}, {@code max}, {@code abs}.</p>
 */
public final class ExpressionParser {

    private List<Token> tokens;
    private int pos;

    /**
     * Разбирает строку в дерево выражения.
     *
     * @param input исходное выражение
     * @return дерево выражения
     * @throws ExpressionParseException если выражение синтаксически некорректно
     */
    public Expr parse(String input) {
        if (input == null) {
            throw new ExpressionParseException("Выражение не может быть пустым", 1);
        }

        this.tokens = tokenize(input);
        this.pos = 0;

        var expression = parseExpression();

        if (current().type() != TokenType.EOF) {
            throw error("Неожиданный символ " + describe(current()), current().position());
        }

        return expression;
    }

    private Expr parseExpression() {
        return parseAdditive();
    }

    private Expr parseAdditive() {
        var left = parseMultiplicative();

        while (current().type() == TokenType.PLUS || current().type() == TokenType.MINUS) {
            var op = switch (current().type()) {
                case PLUS -> BinaryOperator.ADD;
                case MINUS -> BinaryOperator.SUBTRACT;
                default -> throw new IllegalStateException("Внутренняя ошибка при разборе аддитивной операции");
            };

            advance();
            var right = parseMultiplicative();

            left = new BinaryOpNode(op, left, right);
        }

        return left;
    }

    private Expr parseMultiplicative() {
        var left = parseUnary();

        while (current().type() == TokenType.STAR || current().type() == TokenType.SLASH) {
            var op = switch (current().type()) {
                case STAR -> BinaryOperator.MULTIPLY;
                case SLASH -> BinaryOperator.DIVIDE;
                default -> throw new IllegalStateException("Внутренняя ошибка при разборе мультипликативной операции");
            };

            advance();
            var right = parseUnary();

            left = new BinaryOpNode(op, left, right);
        }

        return left;
    }

    private Expr parseUnary() {
        if (current().type() == TokenType.MINUS) {
            advance();
            return new UnaryOpNode(UnaryOperator.NEGATE, parseUnary());
        }

        if (current().type() == TokenType.PLUS) {
            advance();
            return new UnaryOpNode(UnaryOperator.PLUS, parseUnary());
        }

        return parsePower();
    }

    private Expr parsePower() {
        var base = parsePrimary();

        if (current().type() == TokenType.CARET) {
            advance();
            var exponent = parseUnary();
            return new BinaryOpNode(BinaryOperator.POWER, base, exponent);
        }

        return base;
    }

    private Expr parsePrimary() {
        var token = current();

        return switch (token.type()) {
            case NUMBER -> {
                advance();
                yield new NumberNode(Long.parseLong(token.text()));
            }

            case IDENTIFIER -> {
                advance();

                if (current().type() == TokenType.LPAREN) {
                    yield parseFunctionCall(token);
                }

                yield new VariableNode(token.text());
            }

            case LPAREN -> {
                advance();
                var inner = parseExpression();
                expect(TokenType.RPAREN, "Ожидалась закрывающая скобка ')'");
                yield inner;
            }

            default -> throw error("Ожидалось число, переменная, функция или '('", token.position());
        };
    }

    private Expr parseFunctionCall(Token nameToken) {
        var function = Function.byName(nameToken.text())
                .orElseThrow(() -> error("Неизвестная функция: " + nameToken.text(), nameToken.position()));

        advance(); // consume '('

        var args = new ArrayList<Expr>();

        if (current().type() != TokenType.RPAREN) {
            args.add(parseExpression());

            while (current().type() == TokenType.COMMA) {
                advance();
                args.add(parseExpression());
            }
        }

        expect(TokenType.RPAREN, "Ожидалась ')' после аргументов функции");

        if (args.size() != function.arity()) {
            throw error(
                    "Функция " + function.functionName() + " требует " + function.arity() + " аргументов",
                    nameToken.position()
            );
        }

        return new FunctionCallNode(function, args);
    }

    private List<Token> tokenize(String input) {
        var result = new ArrayList<Token>();
        int i = 0;

        while (i < input.length()) {
            char c = input.charAt(i);

            if (Character.isWhitespace(c)) {
                i++;
                continue;
            }

            int position = i + 1;

            if (Character.isDigit(c)) {
                int start = i;

                while (i < input.length() && Character.isDigit(input.charAt(i))) {
                    i++;
                }

                var text = input.substring(start, i);

                try {
                    Long.parseLong(text);
                } catch (NumberFormatException ignored) {
                    throw error("Число не помещается в тип long", position);
                }

                result.add(new Token(TokenType.NUMBER, text, position));
            } else if (Character.isLetter(c) || c == '_') {
                int start = i;

                while (i < input.length()) {
                    char next = input.charAt(i);

                    if (Character.isLetterOrDigit(next) || next == '_') {
                        i++;
                    } else {
                        break;
                    }
                }

                result.add(new Token(TokenType.IDENTIFIER, input.substring(start, i), position));
            } else {
                TokenType type = switch (c) {
                    case '+' -> TokenType.PLUS;
                    case '-' -> TokenType.MINUS;
                    case '*' -> TokenType.STAR;
                    case '/' -> TokenType.SLASH;
                    case '^' -> TokenType.CARET;
                    case '(' -> TokenType.LPAREN;
                    case ')' -> TokenType.RPAREN;
                    case ',' -> TokenType.COMMA;
                    default -> throw error("Неизвестный символ '" + c + "'", position);
                };

                result.add(new Token(type, String.valueOf(c), position));
                i++;
            }
        }

        result.add(new Token(TokenType.EOF, "", input.length() + 1));
        return result;
    }

    private Token current() {
        return tokens.get(pos);
    }

    private Token advance() {
        var token = current();

        if (token.type() != TokenType.EOF) {
            pos++;
        }

        return token;
    }

    private void expect(TokenType type, String message) {
        if (current().type() != type) {
            throw error(message + ", найдено: " + describe(current()), current().position());
        }

        advance();
    }

    private ExpressionParseException error(String message, int position) {
        return new ExpressionParseException(message, position);
    }

    private String describe(Token token) {
        if (token.type() == TokenType.EOF) {
            return "конец выражения";
        }

        return "'" + token.text() + "'";
    }

    private enum TokenType {
        NUMBER,
        IDENTIFIER,
        PLUS,
        MINUS,
        STAR,
        SLASH,
        CARET,
        LPAREN,
        RPAREN,
        COMMA,
        EOF
    }

    private record Token(TokenType type, String text, int position) {
    }
}