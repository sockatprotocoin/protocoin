package net.ddns.protocoin.core.ecdsa;

import net.ddns.protocoin.core.blockchain.Bytable;
import net.ddns.protocoin.core.util.Converter;
import net.ddns.protocoin.core.util.ArrayUtil;

import java.math.BigInteger;

public class Signature implements Bytable {
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

    @Override
    public byte[] getBytes() {
        var paddedR = Converter.bigIntegerToPaddedByteArray(r, 32);
        var paddedS = Converter.bigIntegerToPaddedByteArray(s, 32);

        return ArrayUtil.concat(paddedR, paddedS);
    }
}
