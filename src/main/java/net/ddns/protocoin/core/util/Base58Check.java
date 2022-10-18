package net.ddns.protocoin.core.util;

import java.util.Arrays;

public class Base58Check {
    public static String encode(byte[] bytes) {
        var doubleHash = Hash.sha256(bytes, 2);
        var first4Bytes = Arrays.copyOfRange(doubleHash, 0, 4);
        var extendedBytes = new byte[bytes.length + 4];

        System.arraycopy(bytes, 0, extendedBytes, 0, bytes.length);
        System.arraycopy(first4Bytes, 0, extendedBytes, 20, first4Bytes.length);

        return Base58.encode(extendedBytes);
    }
}
