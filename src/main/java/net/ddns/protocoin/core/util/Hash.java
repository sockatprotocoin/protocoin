package net.ddns.protocoin.core.util;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;

public class Hash {
    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static byte[] sha256(byte[] bytes) {
        return sha256(bytes, 1);
    }

    public static byte[] sha256(byte[] bytes, int rounds) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            System.exit(-1);
        }

        for (int i = 0; i < rounds; i++) {
            bytes = digest.digest(bytes);
        }

        return bytes;
    }

    public static byte[] ripeMD160(byte[] bytes) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("RipeMD160", "BC");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            System.exit(-1);
        }

        return digest.digest(bytes);
    }
}
