package net.ddns.protocoin.ecdsa;

import net.ddns.protocoin.util.ArrayUtil;

import java.math.BigInteger;

public class ECPoint {
    private final BigInteger x;
    private final BigInteger y;

    public ECPoint(BigInteger x, BigInteger y) {
        this.x = x;
        this.y = y;
    }

    public BigInteger getX() {
        return x;
    }

    public BigInteger getY() {
        return y;
    }

    public byte[] toByteArray() {
        return ArrayUtil.concat(x.toByteArray(), y.toByteArray());
    }
}
