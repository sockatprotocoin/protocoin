package net.ddns.protocoin.blockchain.util;

import net.ddns.protocoin.core.blockchain.util.VarInt;
import net.ddns.protocoin.core.util.Converter;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

class VarIntTest {
    @Test
    void oneByteValueTest() {
        var expected = "40";

        var varInt = new VarInt(64);
        var bytes = varInt.getBytes();
        var bytesString = Converter.byteArrayToHexString(bytes);

        assertEquals(expected, bytesString);
    }

    @Test
    void twoByteValueTest() {
        var expected = "fd00c8";

        var varInt = new VarInt(200);
        var bytes = varInt.getBytes();
        var bytesString = Converter.byteArrayToHexString(bytes);

        assertEquals(expected, bytesString);
    }

    @Test
    void threeByteValueTest() {
        var expected = "fe00f42400";

        var varInt = new VarInt(16_000_000);
        var bytes = varInt.getBytes();
        var bytesString = Converter.byteArrayToHexString(bytes);

        assertEquals(expected, bytesString);
    }

    @Test
    void fourByteValueTest() {
        var expected = "fe12345678";

        var varInt = new VarInt(305_419_896);
        var bytes = varInt.getBytes();
        var bytesString = Converter.byteArrayToHexString(bytes);

        assertEquals(expected, bytesString);
    }

//    @Test
//    void fiveByteValueTest() {
//        var expected = "ff000000174876e800";
//
//        var varInt = new VarInt(new BigInteger("100000000000", 10));
//        var bytes = varInt.getBytes();
//        var bytesString = Converter.byteArrayToHexString(bytes);
//
//        assertEquals(expected, bytesString);
//    }
}