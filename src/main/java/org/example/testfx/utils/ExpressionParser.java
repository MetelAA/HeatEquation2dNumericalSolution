package org.example.testfx.utils;

import java.util.*;

/**
 * Оптимизированный парсер выражений.
 * Использование:
 *   ExpressionParser parser = new ExpressionParser();
 *   Expression expr = parser.compile("x^2 + sin(x)");
 *   double y1 = expr.evaluate(2.0);
 *   double y2 = expr.evaluate(3.0);
 *
 * @throws ExpressionException если выражение содержит синтаксические ошибки
 */
public class ExpressionParser {

    // ---------- Публичный API ----------
    @FunctionalInterface
    public interface Expression {
        double evaluate(double x);
    }

    public Expression compile(String expression) throws ExpressionException {
        List<Token> rpn = shuntingYard(tokenize(expression));
        return x -> evalRPN(rpn, x);
    }

    // ---------- Токены (внутренние вспомогательные классы) ----------
    private enum TokenType {
        NUMBER, VARIABLE, OPERATOR, FUNCTION, LEFT_PAREN, RIGHT_PAREN
    }

    private static class Token {
        final TokenType type;
        final String value;   // для операторов, функций, переменной
        final double number;  // для чисел

        Token(TokenType type, String value) {
            this.type = type;
            this.value = value;
            this.number = 0.0;
        }

        Token(double number) {
            this.type = TokenType.NUMBER;
            this.value = null;
            this.number = number;
        }
    }

    // ---------- Токенизация ----------
    private List<Token> tokenize(String s) throws ExpressionException {
        List<Token> tokens = new ArrayList<>();
        int i = 0;
        int len = s.length();

        while (i < len) {
            char c = s.charAt(i);
            if (Character.isWhitespace(c)) {
                i++;
                continue;
            }
            if (Character.isDigit(c) || c == '.') {
                int start = i;
                while (i < len && (Character.isDigit(s.charAt(i)) || s.charAt(i) == '.')) i++;
                double num;
                try {
                    num = Double.parseDouble(s.substring(start, i));
                } catch (NumberFormatException e) {
                    throw new ExpressionException("Некорректное число: " + s.substring(start, i));
                }
                tokens.add(new Token(num));
                continue;
            }
            if (c == 'x') {
                tokens.add(new Token(TokenType.VARIABLE, "x"));
                i++;
                continue;
            }
            if (c == '(') {
                tokens.add(new Token(TokenType.LEFT_PAREN, "("));
                i++;
                continue;
            }
            if (c == ')') {
                tokens.add(new Token(TokenType.RIGHT_PAREN, ")"));
                i++;
                continue;
            }
            if (c == '+' || c == '-' || c == '*' || c == '/' || c == '^') {
                tokens.add(new Token(TokenType.OPERATOR, String.valueOf(c)));
                i++;
                continue;
            }
            if (Character.isLetter(c)) {
                int start = i;
                while (i < len && Character.isLetter(s.charAt(i))) i++;
                String name = s.substring(start, i);
                if (name.equals("sin") || name.equals("cos")) {
                    tokens.add(new Token(TokenType.FUNCTION, name));
                } else {
                    throw new ExpressionException("Неизвестная функция: " + name);
                }
                continue;
            }
            throw new ExpressionException("Недопустимый символ: " + c);
        }
        return tokens;
    }

    // ---------- Алгоритм сортировочной станции (инфикс -> RPN) ----------
    private List<Token> shuntingYard(List<Token> tokens) throws ExpressionException {
        List<Token> output = new ArrayList<>();
        Deque<Token> stack = new ArrayDeque<>();

        Map<String, Integer> precedence = new HashMap<>();
        precedence.put("+", 1);
        precedence.put("-", 1);
        precedence.put("*", 2);
        precedence.put("/", 2);
        precedence.put("^", 3);
        precedence.put("u-", 4); // унарный минус

        for (int i = 0; i < tokens.size(); i++) {
            Token t = tokens.get(i);

            switch (t.type) {
                case NUMBER:
                case VARIABLE:
                    output.add(t);
                    break;

                case FUNCTION:
                    stack.push(t);
                    break;

                case OPERATOR:
                    // Проверка на унарный минус
                    boolean isUnary = t.value.equals("-") && (i == 0 ||
                            tokens.get(i - 1).type == TokenType.OPERATOR ||
                            tokens.get(i - 1).type == TokenType.LEFT_PAREN ||
                            tokens.get(i - 1).type == TokenType.FUNCTION);
                    String op = isUnary ? "u-" : t.value;
                    int currPrec = precedence.get(op);
                    boolean rightAssoc = op.equals("^") || op.equals("u-");

                    while (!stack.isEmpty() && stack.peek().type == TokenType.OPERATOR) {
                        String topOp = stack.peek().value;
                        int topPrec = precedence.get(topOp);
                        if ((rightAssoc && currPrec < topPrec) || (!rightAssoc && currPrec <= topPrec)) {
                            output.add(stack.pop());
                        } else {
                            break;
                        }
                    }
                    stack.push(new Token(TokenType.OPERATOR, op));
                    break;

                case LEFT_PAREN:
                    stack.push(t);
                    break;

                case RIGHT_PAREN:
                    while (!stack.isEmpty() && stack.peek().type != TokenType.LEFT_PAREN) {
                        output.add(stack.pop());
                    }
                    if (stack.isEmpty()) throw new ExpressionException("Несбалансированные скобки");
                    stack.pop(); // удаляем '('
                    if (!stack.isEmpty() && stack.peek().type == TokenType.FUNCTION) {
                        output.add(stack.pop());
                    }
                    break;
            }
        }

        while (!stack.isEmpty()) {
            Token t = stack.pop();
            if (t.type == TokenType.LEFT_PAREN) throw new ExpressionException("Несбалансированные скобки");
            output.add(t);
        }
        return output;
    }

    // ---------- Вычисление RPN для конкретного x ----------
    private double evalRPN(List<Token> rpn, double x) {
        Deque<Double> stack = new ArrayDeque<>();

        for (Token t : rpn) {
            switch (t.type) {
                case NUMBER:
                    stack.push(t.number);
                    break;
                case VARIABLE:
                    stack.push(x);
                    break;
                case OPERATOR:
                    String op = t.value;
                    if (op.equals("u-")) {
                        double a = stack.pop();
                        stack.push(-a);
                    } else {
                        double b = stack.pop();
                        double a = stack.pop();
                        switch (op) {
                            case "+": stack.push(a + b); break;
                            case "-": stack.push(a - b); break;
                            case "*": stack.push(a * b); break;
                            case "/": stack.push(a / b); break;
                            case "^": stack.push(Math.pow(a, b)); break;
                            default: throw new IllegalArgumentException("Неизвестный оператор: " + op);
                        }
                    }
                    break;
                case FUNCTION:
                    String func = t.value;
                    double arg = stack.pop();
                    if (func.equals("sin")) stack.push(Math.sin(arg));
                    else if (func.equals("cos")) stack.push(Math.cos(arg));
                    else throw new IllegalArgumentException("Неизвестная функция: " + func);
                    break;
                default:
                    throw new IllegalArgumentException("Недопустимый токен в RPN");
            }
        }

        if (stack.size() != 1) throw new IllegalArgumentException("Некорректное выражение");
        return stack.pop();
    }

    // ---------- Пользовательское checked исключение ----------
    public static class ExpressionException extends Exception {
        public ExpressionException(String message) {
            super(message);
        }
    }
}