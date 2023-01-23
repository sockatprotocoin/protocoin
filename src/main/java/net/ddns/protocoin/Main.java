package net.ddns.protocoin;

import net.ddns.protocoin.communication.connection.socket.Node;
import net.ddns.protocoin.core.ecdsa.Curve;
import net.ddns.protocoin.core.script.ScriptInterpreter;
import net.ddns.protocoin.eventbus.EventBus;
import net.ddns.protocoin.service.BlockChainService;
import net.ddns.protocoin.service.MiningService;
import net.ddns.protocoin.service.database.UTXOStorage;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private final static EventBus eventBus = new EventBus();
    private final static Curve curve = Curve.secp256k1;
    private final static ScriptInterpreter scriptInterpreter = new ScriptInterpreter(curve);
    private final static UTXOStorage utxoStorage = new UTXOStorage(scriptInterpreter);
    private final static BlockChainService blockChainService = new BlockChainService(utxoStorage, eventBus);
    private final static MiningService miningService = new MiningService(utxoStorage, blockChainService, eventBus);

    private final static Logger logger = Logger.getLogger(Node.class.getName());

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            throw new IllegalArgumentException("Expected 2 arguments: hostname, port");
        }
        var hostname = args[0];
        var port = Integer.parseInt(args[1]);
        logger.log(Level.INFO, "Node working on port: " + port);

        var node = new Node(miningService, eventBus, port);
        node.startListening();
        node.connectToNode(new InetSocketAddress(hostname, port));
        node.startMining(1);
    }
}
