package net.ddns.protocoin.core.blockchain.block;

import net.ddns.protocoin.core.util.Converter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class BlockTest {

    @Test
    void shouldReadBlockFromIsCorrectly() throws IOException, BlockDataException {

        var blockHeader = "028A710E0E36A89223DFA0B36DCCD59FEDD5ECB752E351A05CB264F45D98D6220000000000000000000000000000000000000000000000000000000000000000635E84D0200696F45D41580C";
        var previousBlockHash = "028A710E0E36A89223DFA0B36DCCD59FEDD5ECB752E351A05CB264F45D98D622";
        var transaction = "01FA9EAF04974C2F5A24D139466AE4ACD7BF585F61015DEC033F7711FEF47E18120000000079BE667EF9DCBBAC55A06295CE870B07029BFCDB2DCE28D959F2815B16F817986EF8F2DA3DC5EFFD49D3D4438BD6D121AC1E6851A81CCE2735749166848C2DEA6E17F61F5A05F10A29CF5ECB638069F9A0DB0F97753D046EBB6EA41055FB5316E7FE027B9934A6A31C65E95142BA2678E9045A46F18B12DC8000F73DECCF22B701000000012A05F20019010214D0ECF5C0DCF3797201379ED48F2DE37002FE823C0305";
        var transactionInput = "FA9EAF04974C2F5A24D139466AE4ACD7BF585F61015DEC033F7711FEF47E18120000000079BE667EF9DCBBAC55A06295CE870B07029BFCDB2DCE28D959F2815B16F817986EF8F2DA3DC5EFFD49D3D4438BD6D121AC1E6851A81CCE2735749166848C2DEA6E17F61F5A05F10A29CF5ECB638069F9A0DB0F97753D046EBB6EA41055FB5316E7FE027B9934A6A31C65E95142BA2678E9045A46F18B12DC8000F73DECCF22B7";
        var scriptSig = "79BE667EF9DCBBAC55A06295CE870B07029BFCDB2DCE28D959F2815B16F817986EF8F2DA3DC5EFFD49D3D4438BD6D121AC1E6851A81CCE2735749166848C2DEA6E17F61F5A05F10A29CF5ECB638069F9A0DB0F97753D046EBB6EA41055FB5316E7FE027B9934A6A31C65E95142BA2678E9045A46F18B12DC8000F73DECCF22B7";
        var txid = "FA9EAF04974C2F5A24D139466AE4ACD7BF585F61015DEC033F7711FEF47E1812";
        var transactionOutput = "000000012A05F20019010214D0ECF5C0DCF3797201379ED48F2DE37002FE823C0305";
        var script = "010214D0ECF5C0DCF3797201379ED48F2DE37002FE823C0305";
        var blockIs = "06090609028A710E0E36A89223DFA0B36DCCD59FEDD5ECB752E351A05CB264F45D98D6220000000000000000000000000000000000000000000000000000000000000000635E84D0200696F45D41580C0101FA9EAF04974C2F5A24D139466AE4ACD7BF585F61015DEC033F7711FEF47E18120000000079BE667EF9DCBBAC55A06295CE870B07029BFCDB2DCE28D959F2815B16F817986EF8F2DA3DC5EFFD49D3D4438BD6D121AC1E6851A81CCE2735749166848C2DEA6E17F61F5A05F10A29CF5ECB638069F9A0DB0F97753D046EBB6EA41055FB5316E7FE027B9934A6A31C65E95142BA2678E9045A46F18B12DC8000F73DECCF22B701000000012A05F20019010214D0ECF5C0DCF3797201379ED48F2DE37002FE823C0305";

        var block =  Block.readFromInputStream(new ByteArrayInputStream(Converter.hexStringToByteArray(blockIs)));

        var blockHeaderReceived = Converter.byteArrayToHexString(block.getBlockHeader().getBytes());
        var previousBlockHashReceived = Converter.byteArrayToHexString(block.getBlockHeader().getPreviousBlockHash().getBytes());
        var transactionReceived = block.getTransactions().get(0);
        var transactionInputReceived = transactionReceived.getTransactionInputs().get(0);
        var scriptSigReceived = Converter.byteArrayToHexString(transactionInputReceived.getScriptSignature().getBytes());
        var txidReceived = Converter.byteArrayToHexString(transactionInputReceived.getTxid().getBytes());
        var transactionOutputReceived = transactionReceived.getTransactionOutputs().get(0);
        var scriptReceived = Converter.byteArrayToHexString(transactionOutputReceived.getLockingScript().getBytes());

        Assertions.assertEquals(blockHeader,blockHeaderReceived);
        Assertions.assertEquals(previousBlockHash,previousBlockHashReceived);
        Assertions.assertEquals(transaction,Converter.byteArrayToHexString(transactionReceived.getBytes()));
        Assertions.assertEquals(transactionInput,Converter.byteArrayToHexString(transactionInputReceived.getBytes()));
        Assertions.assertEquals(scriptSig,scriptSigReceived);
        Assertions.assertEquals(txid,txidReceived);
        Assertions.assertEquals(transactionOutput,Converter.byteArrayToHexString(transactionOutputReceived.getBytes()));
        Assertions.assertEquals(script,scriptReceived);
    }

}
