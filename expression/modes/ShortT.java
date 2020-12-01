package expression.modes;

public class ShortT implements Type<Short>{

    public Short add(Short x, Short y) {
        return (short) (x + y);
    }

    public Short subtract(Short x, Short y) {
        return (short) (x - y);
    }

    public Short divide(Short x, Short y) {
        return (short) (x / y);
    }

    public Short multiply(Short x, Short y) {
        return (short) (x * y);
    }

    public Short negate(Short x) {
        return (short) (- x);
    }

    public Short min(Short x, Short y) {
        return (short) Integer.min(x, y);
    }

    public Short max(Short x, Short y) {
        return (short) Integer.max(x, y);
    }

    public Short count(Short x) {
        return (short) (Integer.bitCount(0xFFFF & x));
    }

    public Short parse(String s) {
        return (short) Integer.parseInt(s);
    }
}
