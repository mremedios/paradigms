package expression.operation;

import expression.modes.Type;
import expression.parser.TripleExpression;

public class Negate<T> extends UnaryOperation<T> {

    public Negate(TripleExpression<T> first, Type<T> op) {
        super(first, op);
    }

    public T calc(T a) throws RuntimeException {
        return op.negate(a);
    }
}