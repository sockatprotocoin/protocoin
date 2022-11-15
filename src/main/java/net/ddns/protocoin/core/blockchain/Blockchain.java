package net.ddns.protocoin.core.blockchain;

import net.ddns.protocoin.core.blockchain.block.Block;
import net.ddns.protocoin.core.blockchain.block.BlockDataException;
import net.ddns.protocoin.core.blockchain.block.BlockHeader;
import net.ddns.protocoin.core.blockchain.data.Satoshi;
import net.ddns.protocoin.core.blockchain.transaction.Transaction;
import net.ddns.protocoin.core.blockchain.transaction.TransactionOutput;
import net.ddns.protocoin.core.blockchain.transaction.signature.PayToPubKeyHash;
import net.ddns.protocoin.core.util.ArrayUtil;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Blockchain implements Bytable {
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

    public Blockchain() {
        this.blockchain = new ArrayList<>();
    }

    public Block generateGenesisBlock(byte[] publicKey) {
        var header = new BlockHeader(
                new byte[32],
                new byte[32],
                ByteBuffer.allocate(4).putInt(1666463854).array(),
                new BigInteger("200696f4", 16).toByteArray(),
                new byte[4]
        );
        var transactionOutput = new TransactionOutput(
                Satoshi.valueOf(50.0),
                PayToPubKeyHash.fromPublicKey(publicKey)
        );
        var transaction = new Transaction(
                Collections.emptyList(),
                Collections.singletonList(transactionOutput)
        );

        return new Block(header, Collections.singletonList(transaction));
    }

    public void addBlock(Block block) {
        if (!block.isMined()) {
            throw new IllegalArgumentException("Cannot add unmined block to blockchain!");
        }
        this.blockchain.add(block);
    }

    public Block getTopBlock() {
        return blockchain.get(blockchain.size() - 1);
    }

    public Block getBlock(int height) {
        return blockchain.get(height);
    }

    public List<Block> getBlockList() {
        return blockchain;
    }

    @Override
    public byte[] getBytes() {
        return ArrayUtil.concat(blockchain.stream().map(Block::getBytes).toArray(byte[][]::new));
    }

    public static Blockchain readFromInputStream(InputStream is) throws IOException, BlockDataException {
        var blocks = new ArrayList<Block>();

        while (is.available() > 0) {
            var block = Block.readFromInputStream(is);
            blocks.add(block);
        }

        return new Blockchain(blocks);
    }
}
