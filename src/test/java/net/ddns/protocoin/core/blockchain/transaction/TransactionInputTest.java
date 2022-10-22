package net.ddns.protocoin.core.blockchain.transaction;

import net.ddns.protocoin.core.util.Converter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class TransactionInputTest {

    private final String publicSignatureHex = "647839023ABCD4783920ABCD18390248567584930239485784930203948576584930ABCDE4839203948576584930239485784930239485784930239485784939647839023ABCD4783920ABCD18390248567584930239485784930203948576584930ABCDE4839203948576584930239485784930239485784930239485784939";
    private final String txIdHex = "1253647382910283125364738291028312536473829102831253647382910283";
    private final String voutHex = "FFFABFFF";
    @Test
    void shouldReadFromIsCorrectly() throws IOException {
        var isString = Converter.hexStringToByteArray( txIdHex + voutHex + publicSignatureHex);
        var is = new ByteArrayInputStream(isString);

        var received = TransactionInput.readFromInputStream(is);

        Assertions.assertArrayEquals(Converter.hexStringToByteArray(txIdHex),received.getTxid().getBytes());
        Assertions.assertArrayEquals(Converter.hexStringToByteArray(voutHex),received.getVout().getBytes());
        Assertions.assertArrayEquals(Converter.hexStringToByteArray(publicSignatureHex),received.getScriptSignature().getBytes());
    }

}
