package expression.modes;

public class IntegerT implements Type<Integer> {

    public Integer add(Integer x, Integer y) {
        return x + y;
    }

    public Integer subtract(Integer x, Integer y) {
        return x - y;
    }

    public Integer divide(Integer x, Integer y) {
        return x / y;
    }

    public Integer multiply(Integer x, Integer y) {
        return x * y;
    }

    public Integer negate(Integer x) {
        return - x;
    }

    public Integer min(Integer x, Integer y) {
        return Integer.min(x, y);
    }

    public Integer max(Integer x, Integer y) {
        return Integer.max(x, y);
    }

    public Integer count(Integer x) {
        return Integer.bitCount(x);
    }

    public Integer parse(String s) {
        return Integer.parseInt(s);
    }
}