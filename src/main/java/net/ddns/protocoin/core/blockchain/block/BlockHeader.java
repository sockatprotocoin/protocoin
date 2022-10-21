package net.ddns.protocoin.core.blockchain.block;

import net.ddns.protocoin.core.blockchain.Bytable;
import net.ddns.protocoin.core.blockchain.data.Bytes;
import net.ddns.protocoin.core.util.ArrayUtil;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Arrays;

public class BlockHeader implements Bytable {
    private final Bytes previousBlockHash = new Bytes(32);
    private final Bytes merkleRoot = new Bytes(32);
    private final Bytes timestamp = new Bytes(4);
    private final Bytes targetCompressed = new Bytes(4);
    private final Bytes nonce = new Bytes(4);

    private final BigInteger target;

    public BlockHeader(byte[] previousBlockHash, byte[] merkleRoot, byte[] timestamp, byte[] targetCompressed, byte[] nonce) {
        this.previousBlockHash.setData(previousBlockHash);
        this.merkleRoot.setData(merkleRoot);
        this.timestamp.setData(timestamp);
        this.targetCompressed.setData(targetCompressed);
        this.nonce.setData(nonce);

        this.target = calculateTarget(targetCompressed);
    }

    private BigInteger calculateTarget(byte[] targetCompressed) {
        var exponent = new BigInteger(Arrays.copyOfRange(targetCompressed, 0, 1));
        var coefficient = Arrays.copyOfRange(targetCompressed, 1, 4);

        var targetBytes = new byte[exponent.intValue()];
        System.arraycopy(coefficient, 0, targetBytes, 0, coefficient.length);

        return new BigInteger(targetBytes);
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
