package net.ddns.protocoin.blockchain.transaction;

import net.ddns.protocoin.blockchain.util.VarInt;

import java.math.BigInteger;
import java.util.List;

public class Transaction {
    // powinno byc wyliczalne
    private final VarInt inputCount;
    private final List<TransactionInput> transactionInputs;
    // powinno byc wyliczalne
    private final VarInt outputCount;
    private final List<TransactionOutput> transactionOutputs;

    public Transaction(
            List<TransactionInput> transactionInputs,
            List<TransactionOutput> transactionOutputs
    ) {
        this.inputCount = new VarInt(BigInteger.valueOf(transactionInputs.size()));
        this.transactionInputs = transactionInputs;
        this.outputCount = new VarInt(BigInteger.valueOf(transactionOutputs.size()));
        this.transactionOutputs = transactionOutputs;
    }
}
