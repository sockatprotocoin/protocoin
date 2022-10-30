package net.ddns.protocoin.service;

import net.ddns.protocoin.communication.data.Message;
import net.ddns.protocoin.communication.data.ReqType;
import net.ddns.protocoin.core.blockchain.Blockchain;
import net.ddns.protocoin.core.blockchain.block.Block;
import net.ddns.protocoin.eventbus.EventBus;
import net.ddns.protocoin.eventbus.event.BroadcastNewBlockEvent;
import net.ddns.protocoin.eventbus.listener.BlockchainRequestEventListener;
import net.ddns.protocoin.eventbus.listener.BlockchainResponseEventListener;
import net.ddns.protocoin.eventbus.listener.NewBlockEventListener;
import net.ddns.protocoin.service.database.UTXOStorage;

import java.util.Arrays;

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
        eventBus.registerListener(new BlockchainResponseEventListener(this::loadBlockChainToUTXOStorage));
        eventBus.registerListener(new NewBlockEventListener(this::addBlock));
    }

    public void loadBlockChainToUTXOStorage(Blockchain blockchain) {
        this.blockchain = blockchain;
        utxoStorage.clear();
        for (var block : blockchain.getBlockchain()) {
            var transactions = block.getTransactions();
            if(!topBlockMatchesWith(block) || !blockHashIsBelowTarget(block)){
                return;
            }
            for(var transaction : transactions){
                if(!utxoStorage.verifyTransaction(transaction)){
                    return;
                }
            }
            registerTransactionsFromBlockToUTXOStorage(block);
        }
    }

    public void registerTransactionsFromBlockToUTXOStorage(Block block){
        var transactions = block.getTransactions();
        transactions.forEach(transaction ->
            transaction.getTransactionInputs().forEach(utxoStorage::spentTransactionOutput)
        );
        transactions.forEach(transaction ->
            transaction.getTransactionOutputs().forEach(utxoStorage::addUnspentTransactionOutput)
        );
    }

    public Blockchain getBlockchain() {
        return blockchain;
    }

    public void addBlock(Block newBlock) {
        if(!topBlockMatchesWith(newBlock))
        {
            eventBus.postEvent(new BroadcastNewBlockEvent(new Message(ReqType.BLOCKCHAIN_REQUEST,new byte[]{})));
        }
        if(!blockHashIsBelowTarget(newBlock)){
            return;
        }
        for(var transaction : newBlock.getTransactions()){
            if(!utxoStorage.verifyTransaction(transaction)){
                return;
            }
        }
        blockchain.addBlock(newBlock);
        registerTransactionsFromBlockToUTXOStorage(newBlock);
        eventBus.postEvent(new BroadcastNewBlockEvent(new Message(ReqType.NEW_BLOCK, newBlock.getBytes())));
    }

    public boolean topBlockMatchesWith(Block newBlock){
        var previousBlockHash = newBlock.getBlockHeader().getPreviousBlockHash();
        return Arrays.equals(blockchain.getTopBlock().getHash(), previousBlockHash.getBytes());
    }

    public boolean blockHashIsBelowTarget(Block block){
        //new BigInteger(block.getHash()).compareTo(block.getBlockHeader().getTarget()) < 0 ;
        return block.isMined();
    }
}
