package net.ddns.protocoin.core.blockchain.block;

import net.ddns.protocoin.core.blockchain.data.VarInt;
import net.ddns.protocoin.core.blockchain.transaction.Transaction;
import net.ddns.protocoin.core.util.Converter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class BlockTest {

    @Test
    void shouldReadBlockFromIsCorrectly() throws IOException {
        var headerHex = "0188248B357553E3E9BA82D6549050A056E41597390AE49F5CE168907AE257850000000000000000000000000000000000000000000000000000000000000000635438D4200696F400000000";
        BlockHeader header = BlockHeader.readFromInputStream( new ByteArrayInputStream( Converter.hexStringToByteArray(headerHex)));
        var transactionHex = "01E5CBC4C2F7299C9BE688BF97F622FAEC13AC701439C8C36F856D1131904F91E50000000079BE667EF9DCBBAC55A06295CE870B07029BFCDB2DCE28D959F2815B16F8179879BE667EF9DCBBAC55A06295CE870B07029BFCDB2DCE28D959F2815B16F81798661BA57FED0D115222E30FE7E9509325EE30E7E284D3641E6FB5E67368C2DB185ADA8EFC5DC43AF6BF474A41ED6237573DC4ED693D49102C42FFC8851050079901000000012A05F20018010276B0F3B2743504B9D3E1D04022905548CFEDC7A60305";
        Transaction transaction = Transaction.readFromInputStream(new ByteArrayInputStream(Converter.hexStringToByteArray(transactionHex)));
        VarInt transactionCount = new VarInt(1);

        var block =  Block.readFromInputStream(new ByteArrayInputStream(Converter.hexStringToByteArray(headerHex  + Converter.byteArrayToHexString(transactionCount.getBytes()) + transactionHex)));

        Assertions.assertArrayEquals(header.getBytes(), block.getBlockHeader().getBytes());
        Assertions.assertEquals(transactionCount.integerValue(), block.getTransactions().size());
        Assertions.assertArrayEquals(transaction.getBytes(), block.getTransactions().get(0).getBytes());
    }

}
