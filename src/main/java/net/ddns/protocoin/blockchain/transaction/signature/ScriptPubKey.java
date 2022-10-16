package net.ddns.protocoin.blockchain.transaction.signature;

import net.ddns.protocoin.blockchain.Bytable;

public abstract class ScriptPubKey implements Bytable {
    private final byte[] address;

    public ScriptPubKey(byte[] address) {
        this.address = address;
    }

    public byte[] getAddress() {
        return address;
    }
}
