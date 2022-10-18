package net.ddns.protocoin.util;

import net.ddns.protocoin.core.util.Base58;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Base58Test {
    @Test
    void shouldEncodeBase58Format() {
        var expectedEncoded = "2ABH8YQN4fsKQbAZUB57jVnbXdGiY7zqu";
        var bytes = new byte[] {
                12, -76, 114, -4, -68, 5, -58, 62, -41, 103, 125, -12,
                107, -57, -20, -45, 114, -128, -77, 34, -34, 52, 62, 88,
        };

        var actualEncoded = Base58.encode(bytes);

        assertEquals(expectedEncoded, actualEncoded);
    }
}