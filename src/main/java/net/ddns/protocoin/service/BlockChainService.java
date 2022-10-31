package net.ddns.protocoin.service;

import net.ddns.protocoin.communication.data.Message;
import net.ddns.protocoin.communication.data.ReqType;
import net.ddns.protocoin.core.blockchain.Blockchain;
import net.ddns.protocoin.core.blockchain.block.Block;
import net.ddns.protocoin.core.blockchain.data.Bytes;
import net.ddns.protocoin.core.blockchain.transaction.Transaction;
import net.ddns.protocoin.eventbus.EventBus;
import net.ddns.protocoin.eventbus.event.BroadcastNewBlockEvent;
import net.ddns.protocoin.eventbus.listener.BlockchainRequestEventListener;
import net.ddns.protocoin.eventbus.listener.BlockchainResponseEventListener;
import net.ddns.protocoin.eventbus.listener.NewBlockEventListener;
import net.ddns.protocoin.service.database.UTXOStorage;

import java.util.*;

public class BlockChainService {
    private Blockchain blockchain;
    private final UTXOStorage utxoStorage;
    private final EventBus eventBus;

    public BlockChainService(UTXOStorage utxoStorage, EventBus eventBus) {
        this.utxoStorage = utxoStorage;
        this.eventBus = eventBus;
        setupListeners();
    }

    private void setupListeners() {
        eventBus.registerListener(new BlockchainRequestEventListener(this::getBlockchain));
        eventBus.registerListener(new BlockchainResponseEventListener(this::loadBlockchain));
        eventBus.registerListener(new NewBlockEventListener(newBlock -> {
            addBlock(newBlock);
            eventBus.postEvent(new BroadcastNewBlockEvent(new Message(ReqType.NEW_BLOCK, newBlock.getBytes())));
        }));
    }

    public void loadBlockchain(Blockchain blockchain) {
        utxoStorage.clear();
        this.blockchain = new Blockchain();
        for (var block : blockchain.getBlockchain()) {
                addBlock(block);
        }
    }

    public void addBlock(Block newBlock) {
        try {
            verifyBlockData(newBlock);
        } catch (InvalidPreviousHashException e) {
            eventBus.postEvent(new BroadcastNewBlockEvent(new Message(ReqType.BLOCKCHAIN_REQUEST, new byte[]{})));
            return;
        } catch (HashAboveTargetException | CorruptedTransactionDataException | DoubleSpendException e) {
            return;
        }
        blockchain.addBlock(newBlock);
        registerTransactionsFromBlockToUTXOStorage(newBlock);
    }

    public void registerTransactionsFromBlockToUTXOStorage(Block block) {
        var transactions = block.getTransactions();
        transactions.forEach(transaction ->
                transaction.getTransactionInputs().forEach(utxoStorage::spendTransactionOutput)
        );
        transactions.forEach(transaction ->
                transaction.getTransactionOutputs().forEach(utxoStorage::addUnspentTransactionOutput)
        );
    }

    public Blockchain getBlockchain() {
        return blockchain;
    }

    public void verifyBlockData(Block block) throws InvalidPreviousHashException, HashAboveTargetException, CorruptedTransactionDataException, DoubleSpendException {
        if (!topBlockMatchesWith(block)) {
            throw new InvalidPreviousHashException();
        }
        if (!blockHashIsBelowTarget(block)) {
            throw new HashAboveTargetException();
        }
        for (var transaction : block.getTransactions()) {
            if (!utxoStorage.verifyTransaction(transaction)) {
                throw new CorruptedTransactionDataException();
            }
        }
        var invalidTransactions = getTransactionsUsingSameUTXO(block.getTransactions());
        if (invalidTransactions.size() > 0) {
            throw new DoubleSpendException();
        }
    }

    private boolean topBlockMatchesWith(Block block) {
        var previousBlockHash = block.getBlockHeader().getPreviousBlockHash();
        return Arrays.equals(blockchain.getTopBlock().getHash(), previousBlockHash.getBytes());
    }

    private boolean blockHashIsBelowTarget(Block block) {
        //new BigInteger(block.getHash()).compareTo(block.getBlockHeader().getTarget()) < 0 ;
        return block.isMined();
    }

    public List<Transaction> getTransactionsUsingSameUTXO(List<Transaction> transactions) {
        List<Transaction> invalidTransactions = new ArrayList<>();
        var transactionsInputsMap = new HashMap<Bytes, List<Transaction>>();
        transactions.forEach(transaction ->
                transaction.getTransactionInputs().forEach(
                        input ->
                        {
                            var inputBytes = Bytes.of(input.getBytes(), input.getBytes().length);
                            if (!transactionsInputsMap.containsKey(inputBytes)) {
                                transactionsInputsMap.put(inputBytes, new ArrayList<>());
                            }

                            var transactionsForInput = transactionsInputsMap.get(inputBytes);
                            transactionsForInput.add(transaction);
                        }
                )
        );
        for(Map.Entry<Bytes, List<Transaction>> entry : transactionsInputsMap.entrySet()){
            if(entry.getValue().size() != 1){
                invalidTransactions.addAll(entry.getValue());
            }
        }

        return invalidTransactions;
    }
}
