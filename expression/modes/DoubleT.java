package expression.modes;

public class DoubleT implements Type<Double> {

    public Double add(Double x, Double y) {
        return x + y;
    }

    public Double subtract(Double x, Double y) {
        return x - y;
    }

    public Double divide(Double x, Double y) {
        return x / y;
    }

    public Double multiply(Double x, Double y) {
        return x * y;
    }

    public Double negate(Double x) {
        return - x;
    }

    public Double min(Double x, Double y) {
        return Double.min(x, y);
    }

    public Double max(Double x, Double y) {
        return Double.max(x, y);
    }

    public Double count(Double x) {
        return (double) Long.bitCount(java.lang.Double.doubleToLongBits(x));
    }

    public Double parse(String s) {
        return Double.parseDouble(s);
    }
}
