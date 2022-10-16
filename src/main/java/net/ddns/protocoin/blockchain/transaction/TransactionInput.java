package net.ddns.protocoin.blockchain.transaction;

import net.ddns.protocoin.blockchain.transaction.signature.ScriptSignature;
import net.ddns.protocoin.blockchain.util.VarInt;

import java.math.BigInteger;

public class TransactionInput {
    // hash of previous transaction data (32 bytes)
    private final byte[] txid;
    // 4 bytes
    private final byte[] vout;
    // powinno byc wyliczalne
    private final VarInt scriptSignatureSize;
    private final ScriptSignature scriptSignature;

    public TransactionInput(byte[] txid, byte[] vout, ScriptSignature scriptSignature) {
        this.txid = txid;
        this.vout = vout;
        this.scriptSignatureSize = new VarInt(BigInteger.valueOf(scriptSignature.getBytes().length));
        this.scriptSignature = scriptSignature;
    }
}
