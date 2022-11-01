package net.ddns.protocoin.core.util;

import java.security.SecureRandom;

public class RandomGenerator {
    private final SecureRandom secureRandom = new SecureRandom();

    public byte[] bytes(int numOfBytes) {
        byte[] privateKeyArray = new byte[numOfBytes];
        secureRandom.nextBytes(privateKeyArray);

        return privateKeyArray;
    }
}
