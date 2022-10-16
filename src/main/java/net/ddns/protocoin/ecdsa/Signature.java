package net.ddns.protocoin.ecdsa;

import java.math.BigInteger;

public class Signature {
    private final BigInteger r;
    private final BigInteger s;

    public Signature(BigInteger r, BigInteger s) {
        this.r = r;
        this.s = s;
    }

    public BigInteger getR() {
        return r;
    }

    public BigInteger getS() {
        return s;
    }
}
