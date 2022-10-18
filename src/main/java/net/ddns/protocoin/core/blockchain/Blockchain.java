package net.ddns.protocoin.core.blockchain;

import net.ddns.protocoin.core.blockchain.block.Block;
import net.ddns.protocoin.core.blockchain.block.BlockHeader;
import net.ddns.protocoin.core.blockchain.transaction.Transaction;
import net.ddns.protocoin.core.blockchain.transaction.TransactionInput;
import net.ddns.protocoin.core.blockchain.transaction.TransactionOutput;
import net.ddns.protocoin.core.blockchain.transaction.signature.PayToPubKeyHash;
import net.ddns.protocoin.core.blockchain.transaction.signature.ScriptSignature;
import net.ddns.protocoin.core.blockchain.util.Satoshi;
import net.ddns.protocoin.core.ecdsa.Curve;
import net.ddns.protocoin.core.util.Hash;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Blockchain {
    private final List<Block> blockchain;

    public Blockchain(byte[] publicKey) {
        this.blockchain = new ArrayList<>();
        var genesisBlock = generateGenesisBlock(publicKey);
        genesisBlock.mine();
        addBlock(genesisBlock);
    }

    public Blockchain(List<Block> blocks) {
        this.blockchain = blocks;
    }

    public Block generateGenesisBlock(byte[] publicKey) {
        var header = new BlockHeader(new byte[32], new byte[32], new byte[4], new BigInteger("200696f4", 16).toByteArray(), new byte[4]);
        var transactionInput = new TransactionInput(new byte[32], new byte[4]);
        var transactionOutput = new TransactionOutput(
                Satoshi.valueOf(new BigInteger("5000000000", 10)),
                PayToPubKeyHash.fromPublicKey(publicKey)
                );
        var transaction = new Transaction(
                Collections.singletonList(transactionInput),
                Collections.singletonList(transactionOutput)
        );

        transactionInput.setScriptSignature(new ScriptSignature(new byte[32], publicKey));
        return new Block(header, Collections.singletonList(transaction));
    }

    public void addBlock(Block block) {
        if (!block.isMined()) {
            throw new IllegalArgumentException("Cannot add unmined block to blockchain!");
        }
        this.blockchain.add(block);
        // validate inputs
    }

    public Block getLastBlock() {
        return blockchain.get(blockchain.size() - 1);
    }

    public Block getBlock(int height) {
        return blockchain.get(height);
    }
}
