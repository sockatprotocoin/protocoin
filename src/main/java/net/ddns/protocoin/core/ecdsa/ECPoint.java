package net.ddns.protocoin.core.ecdsa;

import net.ddns.protocoin.core.blockchain.data.Bytes;
import net.ddns.protocoin.core.util.ArrayUtil;

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
        return ArrayUtil.concat(Bytes.of(x.toByteArray(), 32), Bytes.of(y.toByteArray(), 32));
    }
}
