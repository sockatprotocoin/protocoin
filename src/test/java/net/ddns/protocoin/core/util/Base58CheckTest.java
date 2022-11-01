package net.ddns.protocoin.core.util;

import net.ddns.protocoin.core.util.Base58Check;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Base58CheckTest {
    @Test
    void shouldEncodeBase58CheckFormatFirstBytePositive() {
        var bytes = new byte[]{
                12, -76, 114, -4, -68, 5, -58, 62, -41, 103, 125,
                -12, 107, -57, -20, -45, 114, -128, -77, 34
        };
        var base58EncodedExpected = "2ABH8YQN4fsKQbAZUB57jVnbXdGiY7zqu";

        var base58EncodedActual = Base58Check.encode(bytes);

        assertEquals(base58EncodedExpected, base58EncodedActual);
    }

    @Test
    void shouldEncodeBase58CheckFormatFirstByteNegative() {
        var bytes = new byte[]{
                -12, -76, 114, -4, -68, 5, -58, 62, -41, 103, 125,
                -12, 107, -57, -20, -45, 114, -128, -77, 34
        };
        var base58EncodedExpected = "PJt3dp551TyVmZRzKQxJiqXm3mNnVHg7D";

        var base58EncodedActual = Base58Check.encode(bytes);

        assertEquals(base58EncodedExpected, base58EncodedActual);
    }
}