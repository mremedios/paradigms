package expression.parser;

import java.util.Map;

public enum Operation {
    Add, Sub, Mul, Div, Count, Negate, Min, Max;
    private static final Map<Operation, Integer> size = Map.of(
            Add, 1,
            Sub, 1,
            Mul, 1,
            Div, 1,
            Count, 5,
            Min, 3,
            Max, 3,
            Negate, 1
    );

    private static final Map<Operation, Integer> priority = Map.of(
            Add, 3,
            Sub, 3,
            Mul, 2,
            Div, 2,
            Count, 1,
            Min, 4,
            Max, 4,
            Negate, 1
    );

    public int getSize() {
        return size.get(this);
    }

    public int getPriority() {
        return priority.get(this);
    }
}
