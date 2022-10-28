package net.ddns.protocoin.core.blockchain.transaction;

import net.ddns.protocoin.core.blockchain.Bytable;
import net.ddns.protocoin.core.blockchain.data.Satoshi;
import net.ddns.protocoin.core.blockchain.data.VarInt;
import net.ddns.protocoin.core.util.ArrayUtil;
import net.ddns.protocoin.core.util.Hash;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Transaction implements Bytable {
    private final VarInt inputCount;
    private final List<TransactionInput> transactionInputs;
    private final VarInt outputCount;
    private final List<TransactionOutput> transactionOutputs;

    public Transaction(
            List<TransactionInput> transactionInputs,
            List<TransactionOutput> transactionOutputs
    ) {
        this.inputCount = new VarInt(transactionInputs.size());
        this.transactionInputs = transactionInputs;
        this.outputCount = new VarInt(transactionOutputs.size());
        this.transactionOutputs = transactionOutputs;
        transactionOutputs.forEach(output -> output.setParent(this));
    }

    public List<TransactionInput> getTransactionInputs() {
        return transactionInputs;
    }

    public List<TransactionOutput> getTransactionOutputs() {
        return transactionOutputs;
    }

    public byte[] getTxId() {
        return Hash.sha256(getBytes());
    }

    public byte[] getBytesWithoutSignatures() {
        var bytes = transactionInputs.stream()
                .map(TransactionInput::getBytesWithoutSignature)
                .collect(Collectors.toList()).toArray(new byte[0][0]);
        return ArrayUtil.concat(bytes);
    }

    @Override
    public byte[] getBytes() {
        var inputs = ArrayUtil.bytableListToArray(transactionInputs);
        var outputs = ArrayUtil.bytableListToArray(transactionOutputs);

        return ArrayUtil.concat(
                inputCount.getBytes(),
                inputs,
                outputCount.getBytes(),
                outputs
        );
    }

    public static Transaction readFromInputStream(InputStream is) throws IOException {
        var inputCount = VarInt.readFromIs(is);
        var inputs = new ArrayList<TransactionInput>();
        for (int i = 0; i < inputCount.integerValue(); i++) {
            inputs.add(TransactionInput.readFromInputStream(is));
        }

        var outputCount = VarInt.readFromIs(is);
        var outputs = new ArrayList<TransactionOutput>();
        for (int i = 0; i < outputCount.integerValue(); i++) {
            outputs.add(TransactionOutput.readFromInputStream(is));
        }

        return new Transaction(inputs, outputs);
    }

}
