package net.ddns.protocoin.blockchain.transaction.signature;

public class PayToPubkeyHash extends ScriptPubKey {
    public PayToPubkeyHash(byte[] address) {
        super(address);
    }

    @Override
    public byte[] getBytes() {
        var wallet = getAddress();
        // alg do gen scr

        return null;
    }
}
