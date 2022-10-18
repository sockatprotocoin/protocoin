package net.ddns.protocoin.core.blockchain.transaction;

import net.ddns.protocoin.core.blockchain.Bytable;
import net.ddns.protocoin.core.blockchain.transaction.signature.LockingScript;
import net.ddns.protocoin.core.blockchain.transaction.signature.PayToPubKeyHash;
import net.ddns.protocoin.core.blockchain.util.Satoshi;
import net.ddns.protocoin.core.blockchain.util.VarInt;
import net.ddns.protocoin.core.util.ArrayUtil;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;

public class TransactionOutput implements Bytable {
    private final Satoshi amount;
    private final VarInt scriptSize;
    private final LockingScript script;

    private Transaction parent;

    public TransactionOutput(Satoshi amount, LockingScript script) {
        this.amount = amount;
        this.scriptSize = new VarInt(script.getBytes().length);
        this.script = script;
    }

    public void setParent(Transaction parent) {
        this.parent = parent;
    }

    public byte[] getVout() {
        return BigInteger.valueOf(getParent().getTransactionOutputs().indexOf(this)).toByteArray();
    }

    public Transaction getParent() {
        return parent;
    }

    public LockingScript getScript() {
        return script;
    }

    @Override
    public byte[] getBytes() {
        return ArrayUtil.concat(amount.getBytes(), scriptSize.getBytes(), script.getBytes());
    }

    public static TransactionOutput readFromInputStream(InputStream is) throws IOException {
        var amount = Satoshi.readFromInputStream(is);
        var scriptSize = VarInt.readFromIs(is);
        var scriptBytes = is.readNBytes(scriptSize.integerValue());
        var script = PayToPubKeyHash.loadScript(scriptBytes);

        return new TransactionOutput(amount, script);
    }
}
