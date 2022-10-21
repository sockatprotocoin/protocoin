package net.ddns.protocoin.core.blockchain.data;

import net.ddns.protocoin.core.blockchain.Bytable;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Arrays;

public class VarInt implements Bytable {
    private static final byte twoByteMarker = (byte) 0xFD;
    private static final byte fourByteMarker = (byte) 0xFE;
//    private static final byte eightByteMarker = (byte) 0xFF;
    private byte[] data;

    public VarInt(int value) {
        this(BigInteger.valueOf(value));
    }

    public VarInt(BigInteger value) {
        var byteValue = value.toByteArray();

        if (value.compareTo(new BigInteger("fc", 16)) < 0) {
            data = byteValue;
            return;
        } else if (value.compareTo(new BigInteger("ffff", 16)) < 0) {
            data = new byte[3];
            data[0] = twoByteMarker;
        } else if (value.compareTo(new BigInteger("ffffffff", 16)) < 0) {
            data = new byte[5];
            data[0] = fourByteMarker;
//        } else if (value.compareTo(new BigInteger("ffffffffffffffff", 16)) < 0) {
//            data = new byte[9];
//            data[0] = eightByteMarker;
        }

        padWithZeros(data, byteValue);
    }

    public int integerValue() {
        if (data.length == 1) {
            return new BigInteger(Arrays.copyOfRange(data, 0, 1)).intValue();
        } else {
            return new BigInteger(Arrays.copyOfRange(data, 1, data.length)).intValue();
        }
    }

    private void padWithZeros(byte[] array, byte[] value) {
         System.arraycopy(value, 0, array, array.length - value.length, value.length);
    }

    public static VarInt readFromIs(InputStream is) throws IOException {
        var firstByte = (byte) is.read();
        switch (firstByte) {
            case twoByteMarker:
                return new VarInt(new BigInteger(is.readNBytes(2)));
            case fourByteMarker:
                return new VarInt(new BigInteger(is.readNBytes(4)));
//            case eightByteMarker:
//                return new VarInt(new BigInteger(is.readNBytes(8)));
            default:
                return new VarInt(firstByte);
        }
    }

    @Override
    public byte[] getBytes() {
        return data;
    }
}
