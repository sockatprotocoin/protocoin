package net.ddns.protocoin.core.blockchain.block;

import net.ddns.protocoin.core.blockchain.Bytable;
import net.ddns.protocoin.core.blockchain.data.Bytes;
import net.ddns.protocoin.core.util.ArrayUtil;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Arrays;

public class BlockHeader implements Bytable {
    private final Bytes previousBlockHash;
    private final Bytes merkleRoot;
    private final Bytes timestamp;
    private final Bytes targetCompressed;
    private final Bytes nonce;

    private final BigInteger target;

    public BlockHeader(byte[] previousBlockHash, byte[] merkleRoot, byte[] timestamp, byte[] targetCompressed, byte[] nonce) {
        this.previousBlockHash = Bytes.of(previousBlockHash, 32);
        this.merkleRoot = Bytes.of(merkleRoot, 32);
        this.timestamp = Bytes.of(timestamp, 4);
        this.targetCompressed = Bytes.of(targetCompressed, 4);
        this.nonce = Bytes.of(nonce, 4);

        this.target = calculateTarget(targetCompressed);
    }

    private BigInteger calculateTarget(byte[] targetCompressed) {
        var exponent = new BigInteger(Arrays.copyOfRange(targetCompressed, 0, 1));
        var coefficient = Arrays.copyOfRange(targetCompressed, 1, 4);

        var targetBytes = new byte[exponent.intValue()];
        System.arraycopy(coefficient, 0, targetBytes, 0, coefficient.length);

        return new BigInteger(targetBytes);
    }

    public Bytes getPreviousBlockHash() {
        return previousBlockHash;
    }

    public Bytes getMerkleRoot() {
        return merkleRoot;
    }

    public Bytes getTimestamp() {
        return timestamp;
    }

    public Bytes getTargetCompressed() {
        return targetCompressed;
    }

    public Bytes getNonce() {
        return nonce;
    }

    public BigInteger getTarget() {
        return target;
    }

    public void setNonce(byte[] nonce) {
        this.nonce.setData(nonce);
    }

    @Override
    public byte[] getBytes() {
        return ArrayUtil.concat(previousBlockHash, merkleRoot, timestamp, targetCompressed, nonce);
    }

    public static BlockHeader readFromInputStream(InputStream is) throws IOException {
        return new BlockHeader(
            is.readNBytes(32),
            is.readNBytes(32),
            is.readNBytes(4),
            is.readNBytes(4),
            is.readNBytes(4)
        );
    }
}
