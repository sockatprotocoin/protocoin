package net.ddns.protocoin.core.blockchain.transaction;

import net.ddns.protocoin.core.blockchain.Bytable;
import net.ddns.protocoin.core.blockchain.data.Bytes;
import net.ddns.protocoin.core.blockchain.transaction.signature.ScriptSignature;
import net.ddns.protocoin.core.blockchain.data.VarInt;
import net.ddns.protocoin.core.util.ArrayUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class TransactionInput implements Bytable {
    // hash of previous transaction data
    private final Bytes txid = new Bytes(32);
    private final Bytes vout = new Bytes(4);
    private VarInt scriptSignatureSize;
    private ScriptSignature scriptSignature;

    public TransactionInput(byte[] txid, byte[] vout) {
        this.txid.setData(txid);
        this.vout.setData(vout);
    }

    public ScriptSignature getScriptSignature() {
        return scriptSignature;
    }

    public void setScriptSignature(ScriptSignature scriptSignature) {
        this.scriptSignatureSize = new VarInt(scriptSignature.getBytes().length);
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
        return ArrayUtil.concat(txid.getBytes(), vout.getBytes(), scriptSignatureSize.getBytes(), scriptSignature.getBytes());
    }

    public static TransactionInput readFromInputStream(InputStream is) throws IOException {
        var txid = is.readNBytes(32);
        var vout = is.readNBytes(4);
        var scriptSignatureSize = VarInt.readFromIs(is);
        var scriptBytes = is.readNBytes(scriptSignatureSize.integerValue());
        var scriptSignature = new ScriptSignature(Arrays.copyOfRange(scriptBytes, 0, 32), Arrays.copyOfRange(scriptBytes, 32, 64));

        var input = new TransactionInput(txid, vout);
        input.setScriptSignature(scriptSignature);

        return input;
    }
}
