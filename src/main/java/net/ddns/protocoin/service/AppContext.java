package net.ddns.protocoin.service;

import net.ddns.protocoin.communication.connection.socket.Node;
import net.ddns.protocoin.core.ecdsa.Curve;
import net.ddns.protocoin.core.script.ScriptInterpreter;
import net.ddns.protocoin.service.database.UTXOStorage;

public class AppContext {
    private final UTXOStorage utxoStorage;
    private final BlockChainService blockChainService;
    private final MiningService miningService;
    private final Node node;

    private final ScriptInterpreter scriptInterpreter;

    public AppContext() {
        this.scriptInterpreter = new ScriptInterpreter(Curve.secp256k1);
        this.utxoStorage = new UTXOStorage();
        this.blockChainService = new BlockChainService(utxoStorage);
        this.miningService = new MiningService(utxoStorage, this.scriptInterpreter, blockChainService);
        this.node = new Node(blockChainService, miningService);
    }

    public BlockChainService getBlockChainService() {
        return blockChainService;
    }

    public Node getNode() {
        return node;
    }
}
