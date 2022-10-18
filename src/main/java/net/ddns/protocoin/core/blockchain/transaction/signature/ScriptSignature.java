package net.ddns.protocoin.core.blockchain.transaction.signature;

import net.ddns.protocoin.core.blockchain.Bytable;
import net.ddns.protocoin.core.util.ArrayUtil;

public class ScriptSignature implements Bytable {
    private final byte[] signature;
    private final byte[] publicKey;

    public ScriptSignature(byte[] signature, byte[] publicKey) {
        this.signature = signature;
        this.publicKey = publicKey;
    }

    public byte[] getPublicKey() {
        return publicKey;
    }

    @Override
    public byte[] getBytes() {
        return ArrayUtil.concat(signature, publicKey);
    }
}
