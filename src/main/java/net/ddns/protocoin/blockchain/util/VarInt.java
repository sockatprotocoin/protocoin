package net.ddns.protocoin.blockchain.util;

import net.ddns.protocoin.blockchain.Bytable;

import java.math.BigInteger;

public class VarInt implements Bytable {
    private byte[] data;

    public VarInt(BigInteger value) {
        var byteValue = value.toByteArray();

        if (value.compareTo(new BigInteger("fc", 16)) < 0) {
            data = byteValue;
            return;
        } else if (value.compareTo(new BigInteger("ffff", 16)) < 0) {
            data = new byte[3];
            data[0] = (byte) 0xFD;
        } else if (value.compareTo(new BigInteger("ffffffff", 16)) < 0) {
            data = new byte[5];
            data[0] = (byte) 0xFE;
        } else if (value.compareTo(new BigInteger("ffffffffffffffff", 16)) < 0) {
            data = new byte[9];
            data[0] = (byte) 0xFF;
        }

        padWithZeros(data, byteValue);
    }

    private void padWithZeros(byte[] array, byte[] value) {
         System.arraycopy(value, 0, array, array.length - value.length, value.length);
    }

    @Override
    public byte[] getBytes() {
        return data;
    }
}
