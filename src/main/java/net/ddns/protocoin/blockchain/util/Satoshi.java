package net.ddns.protocoin.blockchain.util;

import java.math.BigInteger;

public class Satoshi {
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
}
