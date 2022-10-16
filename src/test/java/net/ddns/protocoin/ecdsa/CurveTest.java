package net.ddns.protocoin.ecdsa;

import net.ddns.protocoin.util.Converter;
import net.ddns.protocoin.util.Hash;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;

class CurveTest {
    private final BigInteger privateKeyMock = new BigInteger("D12D2FACA9AD92828D89683778CB8DFCCDBD6C9E92F6AB7D6065E8AACC1FF6D6", 16);
    private final String publicKeyMock = "661BA57FED0D115222E30FE7E9509325EE30E7E284D3641E6FB5E67368C2DB185ADA8EFC5DC43AF6BF474A41ED6237573DC4ED693D49102C42FFC88510500799";
    private final Signature signatureMock = new Signature(
            new BigInteger("55066263022277343669578718895168534326250603453777594175500187360389116729240", 10),
            new BigInteger("108421160616446705479266723964812845615173331018511864409119033584907646255868", 10)
    );


    Curve curve = Curve.secp256k1;

    @Test
    void shouldCreateCorrectPublicKey() {
        var publicKey = curve.publicKey(privateKeyMock);
        var publicKeyHex = Converter.byteArrayToHexString(publicKey.toByteArray()).toUpperCase();

        assertEquals(publicKeyMock, publicKeyHex);
    }

    @Test
    void shouldGenerateValidSignature() throws NoSuchAlgorithmException {
        var message = Hash.sha256("hello".getBytes());
        var signature = curve.sign(privateKeyMock, message);

        assertEquals(signatureMock.getR().toString(), signature.getR().toString());
        assertEquals(signatureMock.getS().toString(), signature.getS().toString());
    }

    @Test
    void shouldVerifyValidSignature() throws NoSuchAlgorithmException {
        var message = Hash.sha256("hello".getBytes());
        assertTrue(curve.verify(signatureMock, curve.publicKey(privateKeyMock), message));
    }
}