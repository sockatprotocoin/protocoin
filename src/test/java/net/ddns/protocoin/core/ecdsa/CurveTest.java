package net.ddns.protocoin.core.ecdsa;

import net.ddns.protocoin.core.util.Converter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CurveTest {
    private final BigInteger privateKeyMock = new BigInteger("D12D2FACA9AD92828D89683778CB8DFCCDBD6C9E92F6AB7D6065E8AACC1FF6D6", 16);
    private final String publicKeyMock = "661BA57FED0D115222E30FE7E9509325EE30E7E284D3641E6FB5E67368C2DB185ADA8EFC5DC43AF6BF474A41ED6237573DC4ED693D49102C42FFC88510500799";
    private final Signature signatureMock = new Signature(
            new BigInteger("55066263022277343669578718895168534326250603453777594175500187360389116729240", 10),
            new BigInteger("88093338710707192178212899279503648499927231862537303324126888154415358684637", 10)
    );
    Curve curve = Curve.secp256k1;

    @Test
    void shouldCreateCorrectPublicKey() {
        var publicKey = curve.publicKey(privateKeyMock);
        var publicKeyHex = Converter.byteArrayToHexString(publicKey.toByteArray()).toUpperCase();

        assertEquals(publicKeyMock, publicKeyHex);
    }

    @Test
    void shouldGenerateValidSignature() {
        var transactionOutput = "000000012A05F20019010214D0ECF5C0DCF3797201379ED48F2DE37002FE823C0305";

        var signature = curve.sign(privateKeyMock, Converter.hexStringToByteArray(transactionOutput));

        assertEquals(signatureMock.getR().toString(), signature.getR().toString());
        assertEquals(signatureMock.getS().toString(), signature.getS().toString());
    }

    @Test
    void shouldVerifyValidSignature() {
        var transactionOutput = "000000012A05F20019010214D0ECF5C0DCF3797201379ED48F2DE37002FE823C0305";
        var signature = curve.sign(privateKeyMock, Converter.hexStringToByteArray(transactionOutput));
        var publicKey = new ECPoint(
                new BigInteger(1, Arrays.copyOfRange(Converter.hexStringToByteArray(publicKeyMock), 0, 32)),
                new BigInteger(1, Arrays.copyOfRange(Converter.hexStringToByteArray(publicKeyMock), 32, 64))
        );

        var verification = curve.verify(signature,publicKey,Converter.hexStringToByteArray(transactionOutput));

        Assertions.assertTrue(verification);
    }
}