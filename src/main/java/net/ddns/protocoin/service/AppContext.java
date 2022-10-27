package net.ddns.protocoin.service;

import net.ddns.protocoin.communication.connection.socket.Node;

public class AppContext {
    private final BlockChainService blockChainService;
    private final Node node;

    public AppContext() {
        this.blockChainService = new BlockChainService();
        this.node = new Node(blockChainService);
    }

    public BlockChainService getBlockChainService() {
        return blockChainService;
    }

    public Node getNode() {
        return node;
    }
}
