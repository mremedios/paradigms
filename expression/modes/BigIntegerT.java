package expression.modes;

import java.math.BigInteger;

public class BigIntegerT implements Type<BigInteger>{

    public BigInteger add(BigInteger x, BigInteger y) {
        return x.add(y);
    }

    public BigInteger subtract(BigInteger x, BigInteger y) {
        return x.subtract(y);
    }

    public BigInteger divide(BigInteger x, BigInteger y) {
        return x.divide(y);
    }

    public BigInteger multiply(BigInteger x, BigInteger y) {
        return x.multiply(y);
    }

    public BigInteger negate(BigInteger x) {
        return x.negate();
    }

    public BigInteger min(BigInteger x, BigInteger y) {
        return x.min(y);
    }

    public BigInteger max(BigInteger x, BigInteger y) {
        return x.max(y);
    }

    public BigInteger count(BigInteger x) {
        return BigInteger.valueOf(x.bitCount());
    }

    public BigInteger parse(String s) {
        return new BigInteger(s);
    }
}
