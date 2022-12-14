package net.ddns.protocoin.service.database;

import net.ddns.protocoin.core.blockchain.data.Bytes;
import net.ddns.protocoin.core.blockchain.transaction.Transaction;
import net.ddns.protocoin.core.blockchain.transaction.TransactionInput;
import net.ddns.protocoin.core.blockchain.transaction.TransactionOutput;
import net.ddns.protocoin.core.script.ScriptInterpreter;
import net.ddns.protocoin.core.util.Hash;

import java.math.BigInteger;
import java.util.*;

public class UTXOStorage {
    private final HashMap<Bytes, List<TransactionOutput>> map;
    private final ScriptInterpreter scriptInterpreter;

    public UTXOStorage(ScriptInterpreter scriptInterpreter) {
        this.scriptInterpreter = scriptInterpreter;
        map = new HashMap<>();
    }

    public List<TransactionOutput> getUTXOs(byte[] publicKeyHash) {
        var pubKeyBytes = Bytes.of(publicKeyHash, 20);
        if (!map.containsKey(pubKeyBytes)) {
            map.put(pubKeyBytes, new ArrayList<>());
        }
        return map.get(pubKeyBytes);
    }

    public void addUnspentTransactionOutput(TransactionOutput transactionOutput) {
        var pubKey = transactionOutput.getLockingScript().getReceiver();
        var outputList = getUTXOs(pubKey);
        if (!outputList.contains(transactionOutput)) {
            outputList.add(transactionOutput);
        }
    }

    public void spendTransactionOutput(TransactionInput transactionInput) {
        var pubKeyHash = Hash.ripeMD160(Hash.sha256(transactionInput.getScriptSignature().getPublicKey().getBytes()));
        var outputsForPubKeyHash = getUTXOs(pubKeyHash);

        findMatchingUTXOForTransactionInput(outputsForPubKeyHash, transactionInput).ifPresent(outputsForPubKeyHash::remove);
    }

    public void clear() {
        map.clear();
    }

    private Optional<TransactionOutput> findMatchingUTXOForTransactionInput(List<TransactionOutput> outputs, TransactionInput transactionInput) {
        if (outputs == null) {
            return Optional.empty();
        } else {
            return outputs.stream().filter(output ->
                    Arrays.equals(
                            output.getParent().getTxId(), transactionInput.getTxid().getBytes()) &&
                            (output.getVout().equals(transactionInput.getVout()))
            ).findFirst();
        }
    }

    public boolean verifyTransaction(Transaction transaction) {
        List<TransactionOutput> transactionOutputs = new ArrayList<>();
        for (TransactionInput transactionInput : transaction.getTransactionInputs()) {
            var pubKeyHash = Hash.ripeMD160(Hash.sha256(transactionInput.getScriptSignature().getPublicKey().getBytes()));
            var matchingTransactionOutput =
                    findMatchingUTXOForTransactionInput(getUTXOs(pubKeyHash), transactionInput);
            if (matchingTransactionOutput.isEmpty()) {
                return false;
            }
            if (!scriptInterpreter.verify(matchingTransactionOutput.get().getLockingScript(),
                    Hash.sha256(transaction.getBytesWithoutSignatures()), transactionInput.getScriptSignature())) {
                return false;
            }
            transactionOutputs.add(matchingTransactionOutput.get());
        }

        var outputsTotalAmount = getTotalAmountOfOutputs(transactionOutputs);
        var newOutputsTotalAmount = getTotalAmountOfOutputs(transaction.getTransactionOutputs());

        return outputsTotalAmount.compareTo(newOutputsTotalAmount) >= 0;
    }

    public BigInteger getTotalAmountOfOutputs(List<TransactionOutput> outputs) {
        var amount = BigInteger.ZERO;
        for (var transactionOutput : outputs) {
            amount = amount.add(new BigInteger(transactionOutput.getAmount().getBytes()));
        }
        return amount;
    }
}
