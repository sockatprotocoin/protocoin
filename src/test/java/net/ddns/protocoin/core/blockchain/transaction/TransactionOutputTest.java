package net.ddns.protocoin.core.blockchain.transaction;

import net.ddns.protocoin.core.blockchain.data.Satoshi;
import net.ddns.protocoin.core.blockchain.transaction.signature.LockingScript;
import net.ddns.protocoin.core.blockchain.transaction.signature.PayToPubKeyHash;
import net.ddns.protocoin.core.util.Converter;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TransactionOutputTest {
    private final String publicKeyHex = "661BA57FED0D115222E30FE7E9509325EE30E7E284D3641E6FB5E67368C2DB185ADA8EFC5DC43AF6BF474A41ED6237573DC4ED693D49102C42FFC88510500799";

    private final Satoshi amount = Satoshi.valueOf(50.0);
    private final LockingScript lockingScript = PayToPubKeyHash.fromPublicKey(publicKeyHex.getBytes());
    private final byte[] transactionOutputBytes = Converter.hexStringToByteArray(
            "000000012A05F20019010214343B98E5F0860B422D7188F9F774E7C695BFC0AF0305"
    );

    @Test
    void shouldGenerateValidBytes() {
        // when:
        var transactionOutput = new TransactionOutput(amount, lockingScript);
        var actualBytes = transactionOutput.getBytes();

        // then:
        assertArrayEquals(transactionOutputBytes, actualBytes);
    }

    @Test
    void shouldCorrectlyReadTransactionOutputBytes() throws IOException {
        // when:
        var transactionOutput = TransactionOutput.readFromInputStream(new ByteArrayInputStream(transactionOutputBytes));

        // then:
        assertArrayEquals(amount.getBytes(), transactionOutput.getAmount().getBytes());
        assertArrayEquals(lockingScript.getReceiver(), transactionOutput.getLockingScript().getReceiver());
        assertArrayEquals(lockingScript.getScript(), transactionOutput.getLockingScript().getScript());
        assertEquals(lockingScript.getScript().length, transactionOutput.getScriptSize().integerValue());
    }
}