package expression.operation;

import expression.modes.Type;
import expression.parser.TripleExpression;

public class Max<T> extends BinaryOperation<T> {

    public Max(TripleExpression<T> first, TripleExpression<T> second, Type<T> op) {
        super(first, second, op);
    }

    public T calc(T a, T b) throws RuntimeException {
        return op.max(a, b);
    }
}