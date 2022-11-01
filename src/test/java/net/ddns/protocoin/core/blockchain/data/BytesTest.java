package net.ddns.protocoin.core.blockchain.data;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

class BytesTest {
    @Test
    void shouldParseSameSizeArray() {
        // given:
        var data = new byte[]{1, 1, 1};

        // when:
        var bytes = Bytes.of(data, 3);

        // then:
        assertArrayEquals(data, bytes.getBytes());
    }

    @Test
    void shouldParsePlusOneZeroArray() {
        // given:
        var data = new byte[]{0, 1, 1, 1};
        var expectedData = new byte[]{1, 1, 1};

        // when:
        var bytes = Bytes.of(data, 3);

        // then:
        assertArrayEquals(expectedData, bytes.getBytes());
    }

    @Test
    void onWrongSizeArrayShouldThrowException() {
        // given:
        var data = new byte[]{1, 1, 1, 1};

        Executable dataTooLong = () -> Bytes.of(data, 2);
        Executable firstByteNotZero = () -> Bytes.of(data, 3);
        Executable dataTooShort = () -> Bytes.of(data, 5);

        // then:
        assertThrows(IllegalArgumentException.class, dataTooLong);
        assertThrows(IllegalArgumentException.class, firstByteNotZero);
        assertThrows(IllegalArgumentException.class, dataTooShort);
    }

    @Test
    void shouldCorrectlyParseBigIntegers() {
        // given:
        var data1 = new byte[]{0, -17, 0, 0};
        var data2 = new byte[]{-17, 0, 0};
        var data3 = new byte[]{17, 0, 0};

        var expectedValueNegative = BigInteger.valueOf(15663104);
        var expectedValuePositive = BigInteger.valueOf(1114112);

        // when:
        var bytes1 = Bytes.of(data1, 3);
        var bytes2 = Bytes.of(data2, 3);
        var bytes3 = Bytes.of(data3, 3);

        // then:
        assertEquals(expectedValueNegative, bytes1.toBigInteger());
        assertEquals(expectedValueNegative, bytes2.toBigInteger());
        assertEquals(expectedValuePositive, bytes3.toBigInteger());
    }
}