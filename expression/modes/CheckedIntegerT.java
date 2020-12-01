package expression.modes;

import expression.exceptions.*;

public class CheckedIntegerT implements Type<Integer> {

    public Integer add(Integer x, Integer y) throws RuntimeException {
        if (y >= 0 && Integer.MAX_VALUE - y < x || y < 0 && Integer.MIN_VALUE - y > x){
            throw new OverflowException();
        }
        return x + y;
    }
    
    public Integer subtract(Integer x, Integer y) throws RuntimeException {
        if (x >= 0 && (y >= 0 || -Integer.MAX_VALUE + x <= y) ||
                x < 0 && (y <= 0 || x + Integer.MAX_VALUE + 1 >= y)) {
            return x - y;
        }
        throw new OverflowException();
    }
    
    public Integer divide(Integer x, Integer y) throws RuntimeException {
        if (y == 0) {
            throw new DBZException();
        }
        if (x == Integer.MIN_VALUE && y == -1) {
            throw new OverflowException();
        } else {
            return x / y;
        }
    }
    
    public Integer multiply(Integer x, Integer y) throws RuntimeException{
        if (x == 0 || x > 0 && Integer.MIN_VALUE / x <= y && (Integer.MAX_VALUE / x >= y) ||
                x < 0 && Integer.MIN_VALUE / x - 1 >= y - 1 && (Integer.MAX_VALUE / x <= y)) {
            return x * y;
        }
        throw new OverflowException();
    }
    
    public Integer negate(Integer x) {
        if (x == Integer.MIN_VALUE) {
            throw new OverflowException();
        }
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

    public Integer parse (String s) throws RuntimeException {
        return Integer.parseInt(s);
    }
}
