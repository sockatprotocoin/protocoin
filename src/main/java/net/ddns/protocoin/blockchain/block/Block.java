package net.ddns.protocoin.blockchain.block;

import net.ddns.protocoin.blockchain.transaction.Transaction;

import java.util.List;

public class Block {
    private final BlockHeader blockHeader;
    private final List<Transaction> transaction;

    public Block(BlockHeader blockHeader, List<Transaction> transaction) {
        this.blockHeader = blockHeader;
        this.transaction = transaction;
    }
}
