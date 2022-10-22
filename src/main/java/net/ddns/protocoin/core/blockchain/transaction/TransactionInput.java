package net.ddns.protocoin.core.blockchain.transaction;

import net.ddns.protocoin.core.blockchain.Bytable;
import net.ddns.protocoin.core.blockchain.data.Bytes;
import net.ddns.protocoin.core.blockchain.transaction.signature.ScriptSignature;
import net.ddns.protocoin.core.util.ArrayUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class TransactionInput implements Bytable {
    // hash of previous transaction data
    private final Bytes txid;
    private final Bytes vout;
    private ScriptSignature scriptSignature;

    public TransactionInput(byte[] txid, byte[] vout) {
        this.txid = Bytes.of(txid, 32);
        this.vout = Bytes.of(vout, 4);
    }

    public ScriptSignature getScriptSignature() {
        return scriptSignature;
    }

    public void setScriptSignature(ScriptSignature scriptSignature) {
        this.scriptSignature = scriptSignature;
    }

    public Bytes getTxid() {
        return txid;
    }

    public Bytes getVout() {
        return vout;
    }
    public byte[] getBytesWithoutSignature() {
        return ArrayUtil.concat(txid, vout);
    }

    @Override
    public byte[] getBytes() {
        return ArrayUtil.concat(txid.getBytes(), vout.getBytes(), scriptSignature.getBytes());
    }

    public static TransactionInput readFromInputStream(InputStream is) throws IOException {
        var txid = is.readNBytes(32);
        var vout = is.readNBytes(4);
        var scriptBytes = is.readNBytes(128);
        var scriptSignature = new ScriptSignature(Arrays.copyOfRange(scriptBytes, 0, 64), Arrays.copyOfRange(scriptBytes, 64, 128));

        var input = new TransactionInput(txid, vout);
        input.setScriptSignature(scriptSignature);

        return input;
    }
}
