package net.ddns.protocoin.util;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;

public class Hash {
    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static byte[] sha256(byte[] bytes) throws NoSuchAlgorithmException {
        return sha256(bytes, 1);
    }

    public static byte[] sha256(byte[] bytes, int rounds) throws NoSuchAlgorithmException {
        var digest = MessageDigest.getInstance("SHA-256");

        for (int i = 0; i < rounds; i++) {
            bytes = digest.digest(bytes);
        }

        return bytes;
    }

    public static byte[] ripeMD160(byte[] bytes) throws NoSuchProviderException, NoSuchAlgorithmException {
        var digest = MessageDigest.getInstance("RipeMD160", "BC");

        return digest.digest(bytes);
    }
}
