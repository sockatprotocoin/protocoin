package net.ddns.protocoin.service;

import net.ddns.protocoin.communication.connection.socket.Node;
import net.ddns.protocoin.service.database.UTXOStorage;

public class AppContext {
    private final UTXOStorage utxoStorage;
    private final BlockChainService blockChainService;
    private final MiningService miningService;
    private final Node node;

    public AppContext() {
        this.utxoStorage = new UTXOStorage();
        this.blockChainService = new BlockChainService(utxoStorage);
        this.miningService = new MiningService(utxoStorage);
        this.node = new Node(blockChainService, miningService);
    }

    public BlockChainService getBlockChainService() {
        return blockChainService;
    }

    public Node getNode() {
        return node;
    }
}
