package expression.modes;

public class LongT implements Type<Long>{
    public Long add(Long x, Long y) {
        return x + y;
    }

    public Long subtract(Long x, Long y) {
        return x - y;
    }

    public Long divide(Long x, Long y) {
        return x / y;
    }

    public Long multiply(Long x, Long y) {
        return x * y;
    }

    public Long negate(Long x) {
        return -x;
    }

    public Long min(Long x, Long y) {
        return Long.min(x, y);
    }

    public Long max(Long x, Long y) {
        return Long.max(x, y);
    }

    public Long count(Long x) {
        return (long) Long.bitCount(x);
    }

    public Long parse(String s) {
        return Long.parseLong(s);
    }
}
