package expression.parser;

import expression.exceptions.IException;
import expression.modes.Type;
import expression.operation.*;

import java.util.Map;

public class ExpressionParser<T> implements Parser {
    private String source;
    private int pos;
    private int level;
    private Type<T> mode;

    public ExpressionParser(Type<T> mode) {
        this.mode = mode;
    }

    private final Map<String, Operation> stringToOperation = Map.of(
            "+", Operation.Add,
            "-", Operation.Sub,
            "/", Operation.Div,
            "*", Operation.Mul,
            "negate", Operation.Negate,
            "min", Operation.Min,
            "max", Operation.Max,
            "count", Operation.Count
    );

    private TripleExpression<T> toOp(TripleExpression<T> first, TripleExpression<T> second, Operation op) throws IException {
        switch(op){
            case Add: return new Add<>(first, second, mode);
            case Sub: return new Subtract<>(first, second, mode);
            case Mul: return new Multiply<>(first, second, mode);
            case Div: return new Divide<>(first, second, mode);
            case Min: return new Min<>(first, second, mode);
            case Max: return new Max<>(first, second, mode);
            default: throw new IException("wrong operaion");
        }
    }

    private TripleExpression<T> toOp(TripleExpression<T> first, Operation op) throws IException {
        switch(op) {
            case Negate: return new Negate<>(first, mode);
            case Count: return new Count<>(first, mode);
            default: throw new IException("wrong operaion");
        }
    }

    public TripleExpression<T> parse(String expression) throws IException {
        this.source = expression;
        this.pos = 0;
        this.level = 0;
        return parseExpression(10);
    }

    private boolean check(String str) {
        int newpos = pos;
        while (newpos < source.length() && newpos - pos < str.length() && source.charAt(newpos) == str.charAt(newpos - pos)) {
            newpos++;
        }
        if (newpos - pos == str.length()) {
            pos = newpos;
            return true;
        }
        return false;
    }

    private TripleExpression<T> parseElement() throws IException {
        skipWhitespace();
        if (pos >= source.length()) {
            throw new IException("no last element");
        }
        if (source.charAt(pos) == '(') {
            pos++;
            level++;
            TripleExpression<T> bracket = parseExpression(10);
            pos++;
            level--;
            return bracket;
        }
        boolean minus = false;
        if (source.charAt(pos) == 'c') {
            if (check("count")) {
                return toOp(parseExpression((Operation.Count.getPriority())), Operation.Count);
            }
        }
        if (source.charAt(pos) == '-') {
            pos++;
            minus = true;
            if (pos >= source.length()) {
                throw new IException("parsing error");
            }
        }
        if (source.charAt(pos) <= '9' && source.charAt(pos) >= '0') {
            return parseConst(minus);
        }
        if (minus) {
            return toOp(parseExpression((Operation.Negate.getPriority())), Operation.Negate);
        } else {
            return parseVariable();
        }
    }

    private TripleExpression<T> parseExpression(int priority) throws IException {
        TripleExpression<T> first = parseElement();
        while (true) {
            skipWhitespace();
            if (pos >= source.length() || source.charAt(pos) == ')') {
                if (level >= 0) {
                    return first;
                } else {
                    throw new IException("wrong brackets");
                }
            }
            TripleExpression<T> second;
            Operation newop;
            if (stringToOperation.containsKey(source.charAt(pos) + "")) {
                newop = stringToOperation.get(source.charAt(pos) + "");
                pos++;
            } else {
                if (check("min")) {
                    newop = Operation.Min;
                } else if (check("max")) {
                    newop = Operation.Max;
                } else {
                    throw new IException("incorrect operation");
                }
            }
            if (priority <= newop.getPriority()) {
                pos -= newop.getSize();
                return first;
            }
            second = parseExpression(newop.getPriority());
            first = toOp(first, second, newop);
        }
    }


    private TripleExpression<T> parseVariable() {
        int start = pos;
        while (pos < source.length() && Character.isLetter(source.charAt(pos))) {
            pos++;
        }
        return new Variable<>(source.substring(start, pos));
    }

    private TripleExpression<T> parseConst(boolean minus) {
        StringBuilder sb = new StringBuilder();
        if (minus) {
            sb.append("-");
        }
        while (pos < source.length() && source.charAt(pos) <= '9' && source.charAt(pos) >= '0') {
            sb.append(source.charAt(pos++));
        }
        skipWhitespace();
        return new Const<>(mode.parse(sb.toString()));

    }

    private void skipWhitespace() {
        while (pos < source.length() && Character.isWhitespace(source.charAt(pos))) {
            pos++;
        }
    }
}
