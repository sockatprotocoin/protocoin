package net.ddns.protocoin.util;

import net.ddns.protocoin.core.util.RandomGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RandomGeneratorTest {
    private RandomGenerator randomGenerator;

    @BeforeEach
    void setup() {
        this.randomGenerator = new RandomGenerator();
    }

    @Test
    void shouldGenerateRandomBytesOfGivenLength(){
        int targetNumOfBytes = 10;

        var received = randomGenerator.bytes(targetNumOfBytes);

        assertEquals(received.length, targetNumOfBytes);
    }
}