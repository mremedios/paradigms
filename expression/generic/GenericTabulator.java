package expression.generic;

import expression.exceptions.IException;
import expression.modes.*;
import expression.parser.ExpressionParser;
import expression.parser.TripleExpression;

import java.util.Map;

public class GenericTabulator implements Tabulator {

    private final Map<String, Type<?>> modes = Map.of(
            "u", new IntegerT(),
            "i", new CheckedIntegerT(),
            "d", new DoubleT(),
            "l", new LongT(),
            "s", new ShortT(),
            "bi", new BigIntegerT()
    );

    public Object[][][] tabulate(String mode, String expression, int x1, int x2, int y1, int y2, int z1, int z2) throws RuntimeException, IException {
        return table(modes.get(mode), expression, x1, x2, y1, y2, z1, z2);
    }

    private <T> Object[][][] table(Type<T> type, String expression, int x1, int x2, int y1, int y2, int z1, int z2) throws RuntimeException, IException {
        ExpressionParser<T> test = new ExpressionParser<>(type);
        TripleExpression<T> expr = test.parse(expression);
        Object[][][] result = new Object[x2 - x1 + 1][y2 - y1 + 1][z2 - z1 + 1];
        for (int i = x1; i <= x2; i++) {
            for (int j = y1; j <= y2; j++) {
                for (int k = z1; k <= z2; k++) {
                    try {
                        result[i - x1][j - y1][k - z1] =
                                expr.evaluate(type.parse(Integer.toString(i)),
                                        type.parse(Integer.toString(j)),
                                        type.parse(Integer.toString(k)));
                    } catch (RuntimeException e) {
                        result[i - x1][j - y1][k - z1] = null;
                    }
                }
            }
        }
        return result;

    }
}
