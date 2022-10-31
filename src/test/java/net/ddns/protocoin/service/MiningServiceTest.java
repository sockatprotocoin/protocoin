package net.ddns.protocoin.service;

import net.ddns.protocoin.core.blockchain.transaction.Transaction;
import net.ddns.protocoin.eventbus.EventBus;
import net.ddns.protocoin.service.database.UTXOStorage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class MiningServiceTest {
    private BlockChainService blockChainService = Mockito.mock(BlockChainService.class);
    private EventBus eventBus = Mockito.mock(EventBus.class);
    private UTXOStorage utxoStorage = Mockito.mock(UTXOStorage.class);
    private MiningService miningService;

    @BeforeEach
    void setUp(){
        miningService = new MiningService(utxoStorage,blockChainService,eventBus);
    }

    @Test
    void shouldAddTransactionToTransactionPoolIfValid(){
        Transaction transaction = Mockito.mock(Transaction.class);
        Mockito.when(utxoStorage.verifyTransaction(transaction)).thenReturn(true);

        miningService.registerNewTransaction(transaction);

        Assertions.assertTrue(miningService.getTransactionPool().contains(transaction));
    }

    @Test
    void shouldNotAddTransactionToTransactionPoolIfNotValid(){
        Transaction transaction = Mockito.mock(Transaction.class);
        Mockito.when(utxoStorage.verifyTransaction(transaction)).thenReturn(false);

        miningService.registerNewTransaction(transaction);

        Assertions.assertFalse(miningService.getTransactionPool().contains(transaction));
    }
}
