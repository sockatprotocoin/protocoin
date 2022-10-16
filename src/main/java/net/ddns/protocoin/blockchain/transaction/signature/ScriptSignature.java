package net.ddns.protocoin.blockchain.transaction.signature;

import net.ddns.protocoin.blockchain.Bytable;

public class ScriptSignature implements Bytable {
    private final byte[] script;

    public ScriptSignature(byte[] script) {
        this.script = script;
    }

    @Override
    public byte[] getBytes() {
        return script;
    }
}
