package expression.operation;

import expression.modes.Type;
import expression.parser.TripleExpression;

public class Multiply<T> extends BinaryOperation<T> {

    public Multiply(TripleExpression<T> first, TripleExpression<T> second, Type<T> op) {
        super(first, second, op);
    }

    public T calc(T a, T b) throws RuntimeException {
        return op.multiply(a, b);
    }
}