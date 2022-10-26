package net.ddns.protocoin.service;

import net.ddns.protocoin.core.blockchain.Blockchain;
import net.ddns.protocoin.service.database.UTXOStorage;

public class BlockChainService {
    private final UTXOStorage utxoStorage = new UTXOStorage();

    public void loadBlockChainToUTXOStorage(Blockchain blockchain) {
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
}
