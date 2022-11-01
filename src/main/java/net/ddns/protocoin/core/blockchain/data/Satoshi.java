package net.ddns.protocoin.core.blockchain.data;

import net.ddns.protocoin.core.blockchain.Bytable;
import net.ddns.protocoin.core.util.ArrayUtil;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;

public class Satoshi implements Bytable {
    private final static BigDecimal RATE = BigDecimal.valueOf(100_000_000);
    private final Bytes bytes;

    private Satoshi(byte[] bytes) {
        this.bytes = Bytes.of(bytes, 8);
    }

    public static Satoshi valueOf(double btc) throws IllegalStateException {
        var satoshi = RATE.multiply(new BigDecimal(btc)).toBigInteger();
        if (satoshi.compareTo(BigInteger.ZERO) < 0) {
            throw new IllegalStateException("Invalid satoshi amount: " + satoshi);
        }
        var byteArray = ArrayUtil.newByteArrayPaddedWithZeros(8, satoshi.toByteArray());

        return new Satoshi(byteArray);
    }
    @Override
    public byte[] getBytes() {
        return bytes.getBytes();
    }

    public static Satoshi readFromInputStream(InputStream is) throws IOException {
        var satoshiBytes = is.readNBytes(8);
        return new Satoshi(satoshiBytes);
    }
}
