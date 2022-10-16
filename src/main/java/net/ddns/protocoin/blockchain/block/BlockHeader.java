package net.ddns.protocoin.blockchain.block;

public class BlockHeader {
    // 32 bytes
    private final byte[] previousBlockHash;
    // 32 bytes
    private final byte[] merkleRoot;
    // 4 bytes
    private final byte[] timestamp;
    // 4 bytes
    private final byte[] bits;
    // 4 bytes
    private final byte[] nonce;

    public BlockHeader(byte[] previousBlockHash, byte[] merkleRoot, byte[] timestamp, byte[] bits, byte[] nonce) {
        this.previousBlockHash = previousBlockHash;
        this.merkleRoot = merkleRoot;
        this.timestamp = timestamp;
        this.bits = bits;
        this.nonce = nonce;
    }
}
