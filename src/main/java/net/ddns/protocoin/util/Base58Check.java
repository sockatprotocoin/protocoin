package net.ddns.protocoin.util;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class Base58Check {
    public static String encode(byte[] bytes) throws NoSuchAlgorithmException {
        var doubleHash = Hash.sha256(bytes, 2);
        var first4Bytes = Arrays.copyOfRange(doubleHash, 0, 4);
        var extendedBytes = new byte[bytes.length + 4];

        System.arraycopy(bytes, 0, extendedBytes, 0, bytes.length);
        System.arraycopy(first4Bytes, 0, extendedBytes, 20, first4Bytes.length);

        var base58encoded = Base58.encode(extendedBytes);

        return base58encoded;
    }
}
