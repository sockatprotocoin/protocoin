package net.ddns.protocoin.key;

import net.ddns.protocoin.ecdsa.Curve;
import net.ddns.protocoin.ecdsa.ECPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class KeyPairTest {
    private final BigInteger privateKeyMock = new BigInteger("D12D2FACA9AD92828D89683778CB8DFCCDBD6C9E92F6AB7D6065E8AACC1FF6D6", 16);
    private final ECPoint publicKeyMock = new ECPoint(new BigInteger("661BA57FED0D115222E30FE7E9509325EE30E7E284D3641E6FB5E67368C2DB185ADA", 16), new BigInteger("8EFC5DC43AF6BF474A41ED6237573DC4ED693D49102C42FFC88510500799", 16));
    private final String walletAddressMock = "6QZJVCp53qdATUzqxeNiHukroGuHGu3my";
    private Curve curve;
    private KeyPair keyPair;

    @BeforeEach
    void setup() throws NoSuchProviderException, NoSuchAlgorithmException {
        curve = Mockito.mock(Curve.class);
        when(curve.publicKey(privateKeyMock)).thenReturn(publicKeyMock);
        keyPair = new KeyPair(privateKeyMock, curve);
    }

    @Test
    void shouldGenerateValidKeyPair() {
        assertEquals(privateKeyMock, keyPair.getPrivateKey());
        assertEquals(publicKeyMock, keyPair.getPublicKey());
        assertEquals(walletAddressMock, keyPair.getWallet());
    }
}