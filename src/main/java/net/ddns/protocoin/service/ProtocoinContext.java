package net.ddns.protocoin.service;

import net.ddns.protocoin.communication.connection.socket.Node;
import net.ddns.protocoin.core.ecdsa.Curve;
import net.ddns.protocoin.core.script.ScriptInterpreter;
import net.ddns.protocoin.eventbus.EventBus;
import net.ddns.protocoin.service.database.UTXOStorage;

public class ProtocoinContext {
    private final EventBus eventBus;
    private final Curve curve;
    private final ScriptInterpreter scriptInterpreter;
    private final UTXOStorage utxoStorage;
    private final BlockChainService blockChainService;
    private final MiningService miningService;
    private final Node node;

    public ProtocoinContext() {
        this.eventBus = new EventBus();
        this.curve = Curve.secp256k1;
        this.scriptInterpreter = new ScriptInterpreter(curve);
        this.utxoStorage = new UTXOStorage(scriptInterpreter);
        this.blockChainService = new BlockChainService(utxoStorage, eventBus);
        this.miningService = new MiningService(utxoStorage,blockChainService, eventBus);
        this.node = new Node(miningService, eventBus);
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public Curve getCurve() {
        return curve;
    }

    public ScriptInterpreter getScriptInterpreter() {
        return scriptInterpreter;
    }

    public UTXOStorage getUtxoStorage() {
        return utxoStorage;
    }

    public BlockChainService getBlockChainService() {
        return blockChainService;
    }

    public MiningService getMiningService() {
        return miningService;
    }

    public Node getNode() {
        return node;
    }
}
