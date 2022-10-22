package net.ddns.protocoin.core.blockchain.block;

import net.ddns.protocoin.core.util.Converter;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class BlockHeaderTest {
    private final byte[] previousBlockHash = Converter.hexStringToByteArray("01E44019B8BB0394AABB3F1C9ECB834E83F018727EED64A22D8655B97A43A422");
    private final byte[] merkleRoot = Converter.hexStringToByteArray("0000000000000000000000000000000000000000000000000000000000000000");
    // to get epoch int: (int) (System.currentTimeMillis() / 1000) = 1666456760
    private final byte[] timeStamp = ByteBuffer.allocate(4).putInt(1666456760).array();
    private final byte[] targetCompressed = Converter.hexStringToByteArray("200696F4");
    private final byte[] nonce = Converter.hexStringToByteArray("FE40E303");
    private final byte[] blockHeaderBytes = Converter.hexStringToByteArray("01E44019B8BB0394AABB3F1C9ECB834E83F018727EED64A22D8655B97A43A422000000000000000000000000000000000000000000000000000000000000000063541CB8200696F4FE40E303");

    @Test
    void shouldGenerateValidBytes() {
        // when:
        var blockHeader = new BlockHeader(previousBlockHash, merkleRoot, timeStamp, targetCompressed, nonce);
        var actualBytes = blockHeader.getBytes();

        // then:
        assertArrayEquals(blockHeaderBytes, actualBytes);
    }

    @Test
    void shouldCorrectlyReadTransactionOutputBytes() throws IOException {
        // when:
        var blockHeader = BlockHeader.readFromInputStream(new ByteArrayInputStream(blockHeaderBytes));

        // then:
        assertArrayEquals(previousBlockHash, blockHeader.getPreviousBlockHash().getBytes());
        assertArrayEquals(merkleRoot, blockHeader.getMerkleRoot().getBytes());
        assertArrayEquals(targetCompressed, blockHeader.getTargetCompressed().getBytes());
        assertArrayEquals(timeStamp, blockHeader.getTimestamp().getBytes());
        assertArrayEquals(nonce, blockHeader.getNonce().getBytes());
    }
}