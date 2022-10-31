package net.ddns.protocoin.service;

import net.ddns.protocoin.core.blockchain.Blockchain;
import net.ddns.protocoin.core.blockchain.transaction.Transaction;
import net.ddns.protocoin.core.blockchain.transaction.TransactionInput;
import net.ddns.protocoin.core.util.ArrayUtil;
import net.ddns.protocoin.eventbus.EventBus;
import net.ddns.protocoin.service.database.UTXOStorage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

class BlockChainServiceTest {
    private Blockchain blockchain;
    private UTXOStorage utxoStorage;
    private EventBus eventBus;
    private BlockChainService blockChainService;

    @BeforeEach
    void setUp(){
        this.blockchain = Mockito.mock(Blockchain.class);
        this.utxoStorage = Mockito.mock(UTXOStorage.class);
        this.eventBus = Mockito.mock(EventBus.class);
        blockChainService = new BlockChainService(utxoStorage,eventBus);
    }
    @Test
    void shouldVerifyBlocksWhenLoadingBlockchain() {

    }

    @Test
    void shouldReturnTransactionsWithTheSameUTXOs(){
        byte[] txId = new byte[]{1,2,3,4,5,6,7,8,9,10,1,2,3,4,5,6,7,8,9,10,1,2,3,4,5,6,7,8,9,10,1,2};
        byte[] txId2 = new byte[]{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,2,2,2,2,2,2,2,2,2,2,2,3,3};
        byte[] vout = new byte[]{1,2,3,4};
        TransactionInput transactionInput1 = Mockito.mock(TransactionInput.class);
        TransactionInput transactionInput2 = Mockito.mock(TransactionInput.class);
        TransactionInput transactionInput3 = Mockito.mock(TransactionInput.class);
        Mockito.when(transactionInput1.getBytes()).thenReturn(ArrayUtil.concat(txId,vout));
        Mockito.when(transactionInput2.getBytes()).thenReturn(ArrayUtil.concat(txId,vout));
        Mockito.when(transactionInput3.getBytes()).thenReturn(ArrayUtil.concat(txId2,vout));

        Transaction transaction1 = Mockito.mock(Transaction.class);
        Transaction transaction2 = Mockito.mock(Transaction.class);
        Transaction transaction3 = Mockito.mock(Transaction.class);
        Mockito.when(transaction1.getTransactionInputs()).thenReturn(List.of(transactionInput1));
        Mockito.when(transaction2.getTransactionInputs()).thenReturn(List.of(transactionInput2));
        Mockito.when(transaction3.getTransactionInputs()).thenReturn(List.of(transactionInput3));

        var received = blockChainService.getTransactionsUsingSameUTXO(Arrays.asList(transaction1,transaction2,transaction3));

        Assertions.assertTrue(received.contains(transaction1));
        Assertions.assertTrue(received.contains(transaction2));
        Assertions.assertFalse(received.contains(transaction3));
    }
}