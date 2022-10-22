package net.ddns.protocoin.core.blockchain.transaction.signature;

import net.ddns.protocoin.core.util.Converter;
import net.ddns.protocoin.core.util.Hash;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class PayToPubKeyHashTest {
    private final byte[] publicKey = Converter.hexStringToByteArray("661BA57FED0D115222E30FE7E9509325EE30E7E284D3641E6FB5E67368C2DB185ADA8EFC5DC43AF6BF474A41ED6237573DC4ED693D49102C42FFC88510500799");
    private final byte[] publicKeyHashBytes = Hash.ripeMD160(Hash.sha256(publicKey));
    private final byte[] payToPubKeyHashBytes = Converter.hexStringToByteArray("0102d0ecf5c0dcf3797201379ed48f2de37002fe823c0305");

    @Test
    void shouldGenerateValidBytes() {
        var p2pkh = PayToPubKeyHash.fromPublicKey(publicKey);
        var actualBytes = p2pkh.getBytes();

        assertArrayEquals(payToPubKeyHashBytes, actualBytes);
    }

    @Test
    void shouldCorrectlyReadPayToPubKeyHashBytes() {
        // when:
        var p2pkh = PayToPubKeyHash.loadScript(payToPubKeyHashBytes);

        // then:
        assertArrayEquals(publicKeyHashBytes, p2pkh.getReceiver());
        assertArrayEquals(payToPubKeyHashBytes, p2pkh.getScript());
        assertArrayEquals(payToPubKeyHashBytes, p2pkh.getBytes());
    }
}