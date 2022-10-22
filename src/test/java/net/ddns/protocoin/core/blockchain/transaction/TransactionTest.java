package net.ddns.protocoin.core.blockchain.transaction;

import net.ddns.protocoin.core.util.Converter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

public class TransactionTest {
    @Test
    void shouldReadTransactionCorrectlyFromInputStream() throws IOException {
        var transactionOutput = TransactionOutput.readFromInputStream(
                new ByteArrayInputStream(Converter.hexStringToByteArray(
                "000000012A05F200180102343B98E5F0860B422D7188F9F774E7C695BFC0AF0305"
        )));
        var transactionOutput2 =TransactionOutput.readFromInputStream(
                new ByteArrayInputStream(Converter.hexStringToByteArray(
                "000000012A05F200180102343B98E5F0860B422D7188F9F774E7C695BFC0AF0221"
        )));
        var transactionInput = TransactionInput.readFromInputStream(
                new ByteArrayInputStream(Converter.hexStringToByteArray(
                "1253647382910283125364738291028312536473829102831253647382910283FFFFFFFF647839023ABCD4783920ABCD1839" +
                        "0248567584930239485784930203948576584930ABCDE483920394857658493023948578493023948578493023948578" +
                        "4939647839023ABCD4783920ABCD18390248567584930239485784930203948576584930ABCDE4839203948576584930" +
                        "239485784930239485784930239485784939")));
        var transactionInput2 = TransactionInput.readFromInputStream(
                new ByteArrayInputStream(Converter.hexStringToByteArray(
                "1253647382910283125364738291028312536473829102831253647382910283FFFFFFFF647839023ABCD4783920ABCD1839" +
                        "0248567584930239485784930203948576584930ABCDE483920394857658493023948578493023948578493023948578" +
                        "4939647839023ABCD4783920ABCD18390248567584930239485784930203948576584930ABCDE4839203948576584930" +
                        "239485784930239485784930239485784940")));

        var is = new ByteArrayInputStream( new Transaction(List.of(transactionInput,transactionInput2),List.of(transactionOutput,transactionOutput2)).getBytes());
        var received = Transaction.readFromInputStream(is);

        Assertions.assertEquals(2, received.getTransactionInputs().size());
        Assertions.assertEquals(2, received.getTransactionOutputs().size());
        Assertions.assertArrayEquals(transactionInput.getBytes(),received.getTransactionInputs().get(0).getBytes());
        Assertions.assertArrayEquals(transactionInput2.getBytes(),received.getTransactionInputs().get(1).getBytes());
        Assertions.assertArrayEquals(transactionOutput.getBytes(),received.getTransactionOutputs().get(0).getBytes());
        Assertions.assertArrayEquals(transactionOutput2.getBytes(),received.getTransactionOutputs().get(1).getBytes());

    }
}
