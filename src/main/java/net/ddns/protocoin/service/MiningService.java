package net.ddns.protocoin.service;

import net.ddns.protocoin.core.blockchain.block.Block;
import net.ddns.protocoin.core.blockchain.block.BlockHeader;
import net.ddns.protocoin.core.blockchain.transaction.Transaction;
import net.ddns.protocoin.core.util.Converter;
import net.ddns.protocoin.eventbus.EventBus;
import net.ddns.protocoin.eventbus.listener.NewTransactionEventListener;
import net.ddns.protocoin.service.database.UTXOStorage;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class MiningService {
    private final List<Transaction> transactionPool;
    private final BlockChainService blockChainService;
    private final EventBus eventBus;
    private final UTXOStorage utxoStorage;

    public MiningService(UTXOStorage utxoStorage, BlockChainService blockChainService, EventBus eventBus) {
        this.utxoStorage = utxoStorage;
        this.blockChainService = blockChainService;
        this.eventBus = eventBus;
        this.transactionPool = new ArrayList<>();
        setupListeners();
    }

    public void registerNewTransaction(Transaction transaction) {
        if (utxoStorage.verifyTransaction(transaction)) {
            this.transactionPool.add(transaction);
        }
    }

    private void setupListeners() {
        eventBus.registerListener(new NewTransactionEventListener(this::registerNewTransaction));
    }

    public Block startMining() {
        var block = createBlockCandidate();
        block.mine();
        return block;
    }
    public Block createBlockCandidate() {
        var previousBlockHash = blockChainService.getBlockchain().getTopBlock().getHash();
        var timestamp =
                ByteBuffer.allocate(4).putInt((int) (System.currentTimeMillis() / 1000)).array();
        var targetCompressed = Converter.hexStringToByteArray("200696F4");
        BlockHeader blockHeader = new BlockHeader(previousBlockHash,
                new byte[32], timestamp, targetCompressed, new byte[4]);
        return new Block(blockHeader, transactionPool);
    }

    public int getNumberOfWaitingTransactions() {
        return transactionPool.size();
    }
}
