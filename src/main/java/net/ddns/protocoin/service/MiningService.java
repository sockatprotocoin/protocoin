package net.ddns.protocoin.service;

import net.ddns.protocoin.core.blockchain.transaction.Transaction;
import net.ddns.protocoin.service.database.UTXOStorage;

import java.util.ArrayList;
import java.util.List;

public class MiningService {
    private final UTXOStorage utxoStorage;
    private final List<Transaction> transactionPool;

    public MiningService(UTXOStorage utxoStorage) {
        this.utxoStorage = utxoStorage;
        this.transactionPool = new ArrayList<>();
    }

    public void registerNewTransaction(Transaction transaction) {
        this.transactionPool.add(transaction);
    }
}
