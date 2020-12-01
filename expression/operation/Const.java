package expression.operation;

import expression.parser.TripleExpression;

public class Const<T> implements TripleExpression<T> {
    private T cons;

    public Const (T cons) {
        this.cons = cons;
    }

    public T evaluate(T x, T y, T z) throws RuntimeException {
        return cons;
    }
}
