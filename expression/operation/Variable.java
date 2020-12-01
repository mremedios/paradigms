package expression.operation;

import expression.parser.TripleExpression;

public class Variable<T> implements TripleExpression<T> {
    private String var;

    public Variable(String var) {
        this.var = var;
    }
    
    public T evaluate(T x, T y, T z) throws RuntimeException {
        switch (var) {
            case "x":
                return x;
            case "y":
                return y;
            default:
                return z;
        }
    }

}
