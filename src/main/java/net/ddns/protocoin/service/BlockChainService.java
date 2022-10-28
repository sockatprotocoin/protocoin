package net.ddns.protocoin.service;

import net.ddns.protocoin.core.blockchain.Blockchain;
import net.ddns.protocoin.service.database.UTXOStorage;

public class BlockChainService {
    private Blockchain blockchain;
    private final UTXOStorage utxoStorage;

    public BlockChainService(UTXOStorage utxoStorage) {
        this.utxoStorage = utxoStorage;
    }

    public void loadBlockChainToUTXOStorage(Blockchain blockchain) {
        this.blockchain = blockchain;
        utxoStorage.clear();
        for (var block : blockchain.getBlockchain()) {
            var transactions = block.getTransactions();
            transactions.forEach(transaction ->
                    transaction.getTransactionInputs().forEach(utxoStorage::spentTransactionOutput)
            );
            transactions.forEach(transaction ->
                    transaction.getTransactionOutputs().forEach(utxoStorage::addUnspentTransactionOutput)
            );
        }
    }

    public Blockchain getBlockchain() {
        return blockchain;
    }
}
