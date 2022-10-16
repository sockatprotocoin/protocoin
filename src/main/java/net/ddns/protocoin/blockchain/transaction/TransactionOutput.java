package net.ddns.protocoin.blockchain.transaction;

import net.ddns.protocoin.blockchain.transaction.signature.ScriptPubKey;
import net.ddns.protocoin.blockchain.util.Satoshi;
import net.ddns.protocoin.blockchain.util.VarInt;

import java.math.BigInteger;

public class TransactionOutput {
    private final Satoshi amount;
    private final VarInt scriptPubKeySize;
    private final ScriptPubKey scriptPubKey;

    public TransactionOutput(Satoshi amount, ScriptPubKey scriptPubKey) {
        this.amount = amount;
        this.scriptPubKeySize = new VarInt(BigInteger.valueOf(scriptPubKey.getBytes().length));
        this.scriptPubKey = scriptPubKey;
    }
}
