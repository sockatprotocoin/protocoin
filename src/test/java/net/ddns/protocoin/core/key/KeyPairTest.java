package net.ddns.protocoin.core.key;

import net.ddns.protocoin.core.ecdsa.Curve;
import net.ddns.protocoin.core.ecdsa.ECPoint;
import net.ddns.protocoin.core.key.KeyPair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class KeyPairTest {
    private final BigInteger privateKeyMock = new BigInteger("D12D2FACA9AD92828D89683778CB8DFCCDBD6C9E92F6AB7D6065E8AACC1FF6D6", 16);
    private final ECPoint publicKeyMock = new ECPoint(new BigInteger("661BA57FED0D115222E30FE7E9509325EE30E7E284D3641E6FB5E67368C2DB18", 16), new BigInteger("5ADA8EFC5DC43AF6BF474A41ED6237573DC4ED693D49102C42FFC88510500799", 16));
    private final String walletAddressMock = "16QZJVCp53qdATUzqxeNiHukroGuHFJ328";
    private Curve curve;
    private KeyPair keyPair;

    @BeforeEach
    void setup() {
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