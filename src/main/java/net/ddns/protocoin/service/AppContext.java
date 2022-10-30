package net.ddns.protocoin.service;

import net.ddns.protocoin.communication.connection.socket.Node;
import net.ddns.protocoin.core.ecdsa.Curve;
import net.ddns.protocoin.core.script.ScriptInterpreter;
import net.ddns.protocoin.eventbus.EventBus;
import net.ddns.protocoin.service.database.UTXOStorage;

public class AppContext {
    private final EventBus eventBus;
    private final UTXOStorage utxoStorage;
    private final BlockChainService blockChainService;
    private final MiningService miningService;
    private final Node node;

    private final ScriptInterpreter scriptInterpreter;

    public AppContext() {
        this.eventBus = new EventBus();
        this.scriptInterpreter = new ScriptInterpreter(Curve.secp256k1);
        this.utxoStorage = new UTXOStorage();
        this.blockChainService = new BlockChainService(utxoStorage, eventBus);
        this.miningService = new MiningService(utxoStorage, this.scriptInterpreter, blockChainService, eventBus);
        this.node = new Node(miningService, eventBus);
    }

    public BlockChainService getBlockChainService() {
        return blockChainService;
    }

    public Node getNode() {
        return node;
    }
}
