package net.ddns.protocoin.core.blockchain.data;

import net.ddns.protocoin.core.blockchain.Bytable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;

public class VarInt implements Bytable {
    private static final byte oneByteMarker = 0;
    private static final byte twoByteMarker = (byte) 0xFD;
    private static final byte fourByteMarker = (byte) 0xFE;
//    private static final byte eightByteMarker = (byte) 0xFF;

    private final byte marker;
    private final Bytes data;

    public VarInt(int value) {
        this(BigInteger.valueOf(value));
    }

    public VarInt(BigInteger value) {
        var byteValue = value.toByteArray();

        int numOfActualBytes;
        if (value.compareTo(new BigInteger("fc", 16)) < 0) {
            marker = oneByteMarker;
            numOfActualBytes = 1;
        } else if (value.compareTo(new BigInteger("ffff", 16)) < 0) {
            numOfActualBytes = 2;
            marker = twoByteMarker;
        } else if (value.compareTo(new BigInteger("ffffffff", 16)) < 0) {
            numOfActualBytes = 4;
            marker = fourByteMarker;
//        } else if (value.compareTo(new BigInteger("ffffffffffffffff", 16)) < 0) {
//            data = new byte[9];
//            data[0] = eightByteMarker;
        } else {
            throw new IllegalArgumentException("VarInt value out of range");
        }

        this.data = Bytes.of(byteValue, numOfActualBytes);
    }

    public int integerValue() {
        return data.toBigInteger().intValue();
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
        var bs =new ByteArrayOutputStream();
        if (marker != oneByteMarker) {
            bs.write(marker);
        }
        bs.writeBytes(data.getBytes());

        return bs.toByteArray();
    }
}
