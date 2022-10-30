package net.ddns.protocoin.service;

import net.ddns.protocoin.core.blockchain.block.Block;
import net.ddns.protocoin.core.blockchain.block.BlockHeader;
import net.ddns.protocoin.core.blockchain.transaction.Transaction;
import net.ddns.protocoin.core.blockchain.transaction.TransactionInput;
import net.ddns.protocoin.core.blockchain.transaction.TransactionOutput;
import net.ddns.protocoin.core.script.ScriptInterpreter;
import net.ddns.protocoin.core.util.Converter;
import net.ddns.protocoin.core.util.Hash;
import net.ddns.protocoin.eventbus.EventBus;
import net.ddns.protocoin.eventbus.listener.NewTransactionEventListener;
import net.ddns.protocoin.service.database.UTXOStorage;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class MiningService {
    private final UTXOStorage utxoStorage;
    private final List<Transaction> transactionPool;
    private final ScriptInterpreter scriptInterpreter;
    private final BlockChainService blockChainService;
    private final EventBus eventBus;

    public MiningService(UTXOStorage utxoStorage, ScriptInterpreter scriptInterpreter, BlockChainService blockChainService, EventBus eventBus) {
        this.utxoStorage = utxoStorage;
        this.scriptInterpreter = scriptInterpreter;
        this.blockChainService = blockChainService;
        this.eventBus = eventBus;
        this.transactionPool = new ArrayList<>();
        setupListeners();
    }

    public void registerNewTransaction(Transaction transaction) {
        try {
            if (verifyTransaction(transaction)) {
                this.transactionPool.add(transaction);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupListeners() {
        eventBus.registerListener(new NewTransactionEventListener(this::registerNewTransaction));
    }

    public Block startMining() {
        var block = createBlockCandidate();
        block.mine();
        return block;
    }

    private boolean verifyTransaction(Transaction transaction) throws IOException {
        List<TransactionOutput> transactionOutputs = new ArrayList<>();
        for (TransactionInput transactionInput : transaction.getTransactionInputs()) {
            var matchingTransactionOutput =
                    utxoStorage.getMatchingUTXOForTransactionInput(transactionInput);
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

    private BigInteger getTotalAmountOfOutputs(List<TransactionOutput> outputs) {
        var amount = BigInteger.ZERO;
        for (var transactionOutput : outputs) {
            amount = amount.add(new BigInteger(transactionOutput.getAmount().getBytes()));
        }
        return amount;
    }

    public Block createBlockCandidate() {
        var previousBlockHash = blockChainService.getBlockchain().getTopBlock().getHash();
        var timestamp =
                ByteBuffer.allocate(4).putInt((int) (System.currentTimeMillis() / 1000)).array();
        var targetCompressed = Converter.hexStringToByteArray("200696F4");
        BlockHeader blockHeader = new BlockHeader(previousBlockHash,
                new byte[32], timestamp, targetCompressed, new byte[4]);
        return new Block(blockHeader, transactionPool);
    }

    public int getNumberOfWaitingTransactions() {
        return transactionPool.size();
    }
}
