package net.ddns.protocoin.core.blockchain.data;

import net.ddns.protocoin.core.blockchain.Bytable;
import net.ddns.protocoin.core.util.ArrayUtil;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;

public class Satoshi implements Bytable {
    private final Bytes amount = new Bytes(8);

    private Satoshi(byte[] amount) {
        this.amount.setData(amount);
    }

    public static Satoshi valueOf(BigInteger amount) throws IllegalStateException {
        if (amount.compareTo(BigInteger.ZERO) < 0 ) {
            throw new IllegalStateException("Amount cannot be less then 0");
        }
        var byteArray = ArrayUtil.newByteArrayPaddedWithZeros(8, amount.toByteArray());

        return new Satoshi(byteArray);
    }

    @Override
    public byte[] getBytes() {
        return amount.getBytes();
    }

    public static Satoshi readFromInputStream(InputStream is) throws IOException {
        var satoshiBytes = is.readNBytes(8);
        return new Satoshi(satoshiBytes);
    }
}
