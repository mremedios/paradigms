package expression.operation;

import expression.modes.Type;
import expression.parser.TripleExpression;

public abstract class UnaryOperation<T> implements TripleExpression<T> {
    private TripleExpression<T> first;
    protected Type<T> op;

    protected abstract T calc(T a);

    public UnaryOperation(TripleExpression<T> first, Type<T> op) {
        this.first = first;
        this.op = op;
    }

    public T evaluate(T x, T y, T z) {
        return calc(first.evaluate(x, y, z));
    }
}
