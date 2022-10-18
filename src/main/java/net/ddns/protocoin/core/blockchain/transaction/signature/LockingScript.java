package net.ddns.protocoin.core.blockchain.transaction.signature;

import net.ddns.protocoin.core.blockchain.Bytable;

public abstract class LockingScript implements Bytable {
    private final byte[] script;

    public LockingScript(byte[] script) {
        this.script = script;
    }

    public byte[] getScript() {
        return script;
    }

    public abstract byte[] getReceiver();
}
