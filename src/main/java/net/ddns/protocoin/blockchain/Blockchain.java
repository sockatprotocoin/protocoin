package net.ddns.protocoin.blockchain;

import net.ddns.protocoin.blockchain.block.Block;
import net.ddns.protocoin.blockchain.block.BlockHeader;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class Blockchain {
    List<Block> blockchain = new ArrayList<>();

    public Block generateGenesisBlock() {
        return new Block(
                new BlockHeader(
                        BigInteger.ZERO.toByteArray(),
                        null,
                        null,
                        null,
                        null
                ),
                null
            );
    }
}
