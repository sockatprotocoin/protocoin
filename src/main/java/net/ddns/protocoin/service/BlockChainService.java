package net.ddns.protocoin.service;

import net.ddns.protocoin.core.blockchain.Blockchain;
import net.ddns.protocoin.core.blockchain.block.Block;
import net.ddns.protocoin.service.database.UTXOStorage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class BlockChainService {
    private final UTXOStorage utxoStorage = new UTXOStorage();

    public Blockchain loadBlockChain(BlockChainSource blockChainSource) {
        var blocks = new ArrayList<Block>();

        try {
            var is = blockChainSource.getBlockChainInputStream();
            while (is.available() > 0) {
                if (Arrays.equals(is.readNBytes(4), Block.MAGIC_BYTES)) {
                    var block = Block.readFromInputStream(is);
                    blocks.add(block);
                    var transactions = block.getTransactions();
                    transactions.forEach(transaction ->
                            transaction.getTransactionOutputs().forEach(utxoStorage::addUnspentTransactionOutput)
                    );
                    transactions.forEach(transaction ->
                            transaction.getTransactionInputs().forEach(utxoStorage::spentTransactionOutput)
                    );
                }
            }

            return new Blockchain(blocks);
        } catch (IOException e) {
            System.exit(-1);
            return null;
        }
    }
}
