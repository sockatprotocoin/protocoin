package net.ddns.protocoin.service.database;

import net.ddns.protocoin.core.blockchain.data.Bytes;
import net.ddns.protocoin.core.blockchain.transaction.TransactionInput;
import net.ddns.protocoin.core.blockchain.transaction.TransactionOutput;
import net.ddns.protocoin.core.util.Hash;

import java.util.*;

public class UTXOStorage {
    private final HashMap<Bytes, List<TransactionOutput>> map;

    public UTXOStorage() {
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
        var pubKeyBytes = Bytes.of(pubKey, 20);
        if (!map.containsKey(pubKeyBytes)) {
            var outputList = new ArrayList<TransactionOutput>();
            map.put(pubKeyBytes, outputList);
        }
        var outputList = map.get(pubKeyBytes);
        if (!outputList.contains(transactionOutput)) {
            outputList.add(transactionOutput);
        }
    }

    public void spentTransactionOutput(TransactionInput transactionInput) {
        var pubKeyHash = Hash.ripeMD160(Hash.sha256(transactionInput.getScriptSignature().getPublicKey().getBytes()));
        var pubKeyBytes = Bytes.of(pubKeyHash, 20);
        var outputsForPubKeyHash = map.get(pubKeyBytes);

        getMatchingUTXOForTransactionInput(transactionInput).ifPresent(outputsForPubKeyHash::remove);
    }
    public void clear() {
        map.clear();
    }

    public Optional<TransactionOutput> getMatchingUTXOForTransactionInput(TransactionInput transactionInput){
        var pubKeyHash = Hash.ripeMD160(Hash.sha256(transactionInput.getScriptSignature().getPublicKey().getBytes()));
        var pubKeyBytes = Bytes.of(pubKeyHash, 20);
        var outputsForPubKeyHash = map.get(pubKeyBytes);

        if(outputsForPubKeyHash == null){
            return Optional.empty();
        }
        else {
            return outputsForPubKeyHash.stream().filter(output ->
                    Arrays.equals(
                            output.getParent().getTxId(), transactionInput.getTxid().getBytes()) &&
                            (output.getVout().equals(transactionInput.getVout()))
            ).findFirst();
        }
    }
}
