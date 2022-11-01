package net.ddns.protocoin.core.blockchain.data;

import net.ddns.protocoin.core.blockchain.Bytable;

import java.math.BigInteger;
import java.util.Arrays;

public class Bytes implements Bytable {
    private byte[] data;

    public Bytes(int size) {
        this.data = new byte[size];
    }

    private Bytes(byte[] data) {
        this.data = data;
    }

    public static Bytes of(byte[] data, int length) {
        return new Bytes(parseByteData(data, length));
    }

    private static byte[] parseByteData(byte[] data, int length) {
        var destArray = new byte[length];
        int offset = 0;

        if (isDataSizeNotExact(data, length)) {
            if (isOnlyOneExtraZeroPresent(data, length)) {
                offset++;
            } else {
                throw new IllegalArgumentException();
            }
        }
        System.arraycopy(data, offset, destArray, 0, length);

        return destArray;
    }

    private static boolean isDataSizeNotExact(byte[] data, int length) {
        return data.length != length;
    }

    private static boolean isOnlyOneExtraZeroPresent(byte[] data, int length) {
        return data.length == length + 1 && data[0] == 0;
    }

    public void setData(byte[] data) {
        this.data = parseByteData(data, this.data.length);
    }

    public BigInteger toBigInteger() {
        return new BigInteger(1, data);
    }

    @Override
    public byte[] getBytes() {
        return data;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        var bytes = (Bytes) obj;
        return Arrays.equals(this.getBytes(), bytes.getBytes());
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.getBytes());
    }
}
