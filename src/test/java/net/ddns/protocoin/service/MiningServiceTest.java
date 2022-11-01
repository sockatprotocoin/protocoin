package net.ddns.protocoin.service;

import net.ddns.protocoin.core.blockchain.Blockchain;
import net.ddns.protocoin.core.blockchain.block.Block;
import net.ddns.protocoin.core.blockchain.transaction.Transaction;
import net.ddns.protocoin.eventbus.EventBus;
import net.ddns.protocoin.service.database.UTXOStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MiningServiceTest {
    private BlockChainService blockChainService = mock(BlockChainService.class);
    private EventBus eventBus = mock(EventBus.class);
    private UTXOStorage utxoStorage = mock(UTXOStorage.class);
    private MiningService miningService;

    @BeforeEach
    void setUp(){
        miningService = new MiningService(utxoStorage,blockChainService,eventBus);
    }

    @Test
    void shouldAddTransactionToTransactionPoolIfValid(){
        var transaction = mock(Transaction.class);
        when(utxoStorage.verifyTransaction(transaction)).thenReturn(true);

        miningService.registerNewTransaction(transaction);

        assertTrue(miningService.getTransactionPool().contains(transaction));
    }

    @Test
    void shouldNotAddTransactionToTransactionPoolIfNotValid(){
        var transaction = mock(Transaction.class);
        when(utxoStorage.verifyTransaction(transaction)).thenReturn(false);

        miningService.registerNewTransaction(transaction);

        assertFalse(miningService.getTransactionPool().contains(transaction));
    }

    @Test
    void shouldCreateBlockCandidateFromPooledTransaction() {
        var blockchainMock = mock(Blockchain.class);
        var topBlockMock = mock(Block.class);
        var blockHash = new byte[32];
        when(blockChainService.getBlockchain()).thenReturn(blockchainMock);
        when(blockchainMock.getTopBlock()).thenReturn(topBlockMock);
        when(topBlockMock.getHash()).thenReturn(blockHash);
        when(utxoStorage.verifyTransaction(any())).thenReturn(true);
        var transaction1 = mock(Transaction.class);
        when(transaction1.getBytes()).thenReturn(new byte[1]);
        var transaction2 = mock(Transaction.class);
        when(transaction2.getBytes()).thenReturn(new byte[1]);

        miningService.registerNewTransaction(transaction1);
        miningService.registerNewTransaction(transaction2);
        var block = miningService.startMining();

        assertEquals(transaction1, block.getTransactions().get(0));
        assertEquals(transaction2, block.getTransactions().get(1));
    }
}
