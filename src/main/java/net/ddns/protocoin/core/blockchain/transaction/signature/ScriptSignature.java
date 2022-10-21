package net.ddns.protocoin.core.blockchain.transaction.signature;

import net.ddns.protocoin.core.blockchain.Bytable;
import net.ddns.protocoin.core.blockchain.data.Bytes;
import net.ddns.protocoin.core.util.ArrayUtil;

public class ScriptSignature implements Bytable {
    private final Bytes signature = new Bytes(64);
    private final Bytes publicKey = new Bytes(64);

    public ScriptSignature(byte[] signature, byte[] publicKey) {
        this.signature.setData(signature);
        this.publicKey.setData(publicKey);
    }

    public Bytes getPublicKey() {
        return publicKey;
    }

    @Override
    public byte[] getBytes() {
        return ArrayUtil.concat(signature, publicKey);
    }
}
