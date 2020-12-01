package expression.operation;

import expression.modes.Type;
import expression.parser.TripleExpression;

public class Count<T> extends UnaryOperation<T> {

    public Count(TripleExpression<T> first, Type<T> op) {
        super(first, op);
    }

    public T calc(T a) throws RuntimeException {
        return op.count(a);
    }
}
