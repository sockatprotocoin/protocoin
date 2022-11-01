package net.ddns.protocoin.core.blockchain.data;

import net.ddns.protocoin.core.util.ArrayUtil;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class SatoshiTest {
    @Test
    void shouldCorrectlyConvertBTCToSatoshi() {
        // given:
        var bytesExpected = ArrayUtil.newByteArrayPaddedWithZeros(8, new BigInteger("5000000000", 10).toByteArray());

        // when:
        var fiftyBTC = Satoshi.valueOf(50.0);

        // then:
        assertArrayEquals(bytesExpected, fiftyBTC.getBytes());
    }
}