package net.ddns.protocoin.core.util;

import net.ddns.protocoin.core.util.Converter;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ConverterTest {
    private final String hexString = "046B38EAD2824C64FA394D8641B365FEE17A4E7B2D6D292065FDFE4E3A0FD8F88244DAC26F13B1BA68C115C2AA21A1B690CBD37C4331783DE6EAE30A17CD0EC3F2";
    private final byte[] bytesOfHexString = new byte[]{
            4, 107, 56, -22, -46, -126, 76, 100, -6, 57, 77, -122, 65, -77,
            101, -2, -31, 122, 78, 123, 45, 109, 41, 32, 101, -3, -2, 78,
            58, 15, -40, -8, -126, 68, -38, -62, 111, 19, -79, -70, 104, -63,
            21, -62, -86, 33, -95, -74, -112, -53, -45, 124, 67, 49, 120, 61,
            -26, -22, -29, 10, 23, -51, 14, -61, -14,
    };

    @Test
    void shouldCorrectlyConvertBytesToHexString() {
        var afterConversion = Converter.byteArrayToHexString(bytesOfHexString);

        assertEquals(hexString, afterConversion.toUpperCase());
    }

    @Test
    void shouldCorrectlyConvertHexStringToBytes() {
        var afterConversion = Converter.hexStringToByteArray(hexString);

        assertArrayEquals(bytesOfHexString, afterConversion);
    }

    @Test
    void shouldPadToByteArrayOfTargetSize(){
        var bigIntegerValue = new BigInteger(hexString,16);

        var expected = new byte[]{
                0, 0, 0, 0, 0,
                4, 107, 56, -22, -46, -126, 76, 100, -6, 57, 77, -122, 65, -77,
                101, -2, -31, 122, 78, 123, 45, 109, 41, 32, 101, -3, -2, 78,
                58, 15, -40, -8, -126, 68, -38, -62, 111, 19, -79, -70, 104, -63,
                21, -62, -86, 33, -95, -74, -112, -53, -45, 124, 67, 49, 120, 61,
                -26, -22, -29, 10, 23, -51, 14, -61, -14,
        };

        var received = Converter.bigIntegerToPaddedByteArray(bigIntegerValue,70);

        assertArrayEquals(expected, received);
    }
}