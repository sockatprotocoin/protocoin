package net.ddns.protocoin.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import static org.junit.jupiter.api.Assertions.*;

class HashTest {
    @Test
    void sha256oneRound() throws NoSuchAlgorithmException {
        // https://gobittest.appspot.com/Address
        String publicKey = "04661BA57FED0D115222E30FE7E9509325EE30E7E284D3641E6FB5E67368C2DB185ADA8EFC5DC43AF6BF474A41ED6237573DC4ED693D49102C42FFC88510500799";
        String hashExpected = "FC84B264854D7402D6272899CDF060430C54CDEC949193116FC34E93D2284F23";

        var hashBytes = Hash.sha256(Converter.hexStringToByteArray(publicKey));
        var hashString = Converter.byteArrayToHexString(hashBytes);

        Assertions.assertEquals(hashExpected, hashString);
    }

    @Test
    void testSha256twoRounds() throws NoSuchAlgorithmException {
        // https://gobittest.appspot.com/Address
        String publicKey = "04661BA57FED0D115222E30FE7E9509325EE30E7E284D3641E6FB5E67368C2DB185ADA8EFC5DC43AF6BF474A41ED6237573DC4ED693D49102C42FFC88510500799";
        String hashExpected = "B0AD2AF5DCC14B5F1BEECDE430D1DE7DA8FA3D0EEE4DBB0106FC0264B0C9119F";

        var hashBytes = Hash.sha256(Converter.hexStringToByteArray(publicKey), 2);
        var hashString = Converter.byteArrayToHexString(hashBytes);

        Assertions.assertEquals(hashExpected, hashString);
    }

    @Test
    void ripeMD160() throws NoSuchProviderException, NoSuchAlgorithmException {
        // https://gobittest.appspot.com/Address
        String hash1 = "FC84B264854D7402D6272899CDF060430C54CDEC949193116FC34E93D2284F23";
        String hash2Expected = "3B4D28F363DAAD311AEF7D422872615F48F31435";

        var hashBytes = Hash.ripeMD160(Converter.hexStringToByteArray(hash1));
        var hashString = Converter.byteArrayToHexString(hashBytes);

        Assertions.assertEquals(hash2Expected, hashString);
    }
}