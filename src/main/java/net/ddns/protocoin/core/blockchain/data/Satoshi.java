package net.ddns.protocoin.core.blockchain.data;

import net.ddns.protocoin.core.blockchain.Bytable;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;

public class Satoshi implements Bytable {
    private final byte[] amount;

    private Satoshi(byte[] amount) {
        this.amount = amount;
    }

    public static Satoshi valueOf(BigInteger amount) throws IllegalStateException {
        if (amount.compareTo(BigInteger.ZERO) < 0 ) {
            throw new IllegalStateException("Amount cannot be less then 0");
        }

        return new Satoshi(amount.toByteArray());
    }

    @Override
    public byte[] getBytes() {
        return amount;
    }

    public static Satoshi readFromInputStream(InputStream is) throws IOException {
        var satoshiBytes = is.readNBytes(8);
        return new Satoshi(satoshiBytes);
    }
}
