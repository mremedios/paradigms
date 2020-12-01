package expression.operation;

import expression.modes.Type;
import expression.parser.TripleExpression;

public abstract class BinaryOperation<T> implements TripleExpression<T> {
    private TripleExpression<T> first;
    private TripleExpression<T> second;
    protected Type<T> op;

    protected abstract T calc(T a, T b);

    public BinaryOperation(TripleExpression<T> first, TripleExpression<T> second, Type<T> op) {
        this.first = first;
        this.second = second;
        this.op = op;
    }

    public T evaluate(T x, T y, T z) {
        return calc(first.evaluate(x, y, z), second.evaluate(x, y, z));
    }
}
