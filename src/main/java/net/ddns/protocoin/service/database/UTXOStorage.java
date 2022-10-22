package net.ddns.protocoin.service.database;

import net.ddns.protocoin.core.blockchain.transaction.TransactionInput;
import net.ddns.protocoin.core.blockchain.transaction.TransactionOutput;
import net.ddns.protocoin.core.util.Hash;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class UTXOStorage {
    private final HashMap<byte[], List<TransactionOutput>> map;

    public UTXOStorage() {
        map = new HashMap<>();
    }

    public List<TransactionOutput> getUTXOs(byte[] publicKey) {
        return map.get(publicKey);
    }

    public void addUnspentTransactionOutput(TransactionOutput transactionOutput) {
        var pubKey = transactionOutput.getLockingScript().getReceiver();
        if (!map.containsKey(pubKey)) {
            map.put(pubKey, Collections.singletonList(transactionOutput));
        } else {
            var outputList = map.get(pubKey);
            if (!outputList.contains(transactionOutput)) {
                outputList.add(transactionOutput);
            }
        }
    }

    public void spentTransactionOutput(TransactionInput transactionInput) {
        var pubKeyHash = Hash.ripeMD160(Hash.sha256(transactionInput.getScriptSignature().getPublicKey().getBytes()));
        var outputsForPubKeyHash = map.get(pubKeyHash);
        var matchingUnspentOutput = outputsForPubKeyHash.stream().filter(output ->
                Arrays.equals(
                        output.getParent().getTxId(), transactionInput.getTxid().getBytes()) &&
                        (output.getVout() == transactionInput.getVout().getBytes()
                )
        ).findFirst();

        matchingUnspentOutput.ifPresent(outputsForPubKeyHash::remove);
    }
}
