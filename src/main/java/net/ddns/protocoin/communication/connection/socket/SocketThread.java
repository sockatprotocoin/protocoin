package net.ddns.protocoin.communication.connection.socket;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.ddns.protocoin.communication.connection.DataMiddleware;
import net.ddns.protocoin.communication.connection.MessageMiddleware;
import net.ddns.protocoin.communication.data.Message;
import net.ddns.protocoin.communication.data.ReqType;
import net.ddns.protocoin.core.blockchain.Blockchain;
import net.ddns.protocoin.core.blockchain.block.Block;
import net.ddns.protocoin.core.blockchain.block.BlockDataException;
import net.ddns.protocoin.core.blockchain.transaction.Transaction;
import net.ddns.protocoin.core.util.ArrayUtil;
import net.ddns.protocoin.service.BlockChainService;
import net.ddns.protocoin.service.MiningService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

public class SocketThread extends Thread {
    private static final Logger logger = LogManager.getLogger(SocketThread.class);
    private final Node node;
    private final BlockChainService blockChainService;
    private final MiningService miningService;
    private final Socket socket;
    private final DataMiddleware<InputStream, Message> dataMiddleware;
    private OutputStream out;

    private boolean running;

    public SocketThread(Node node, BlockChainService blockChainService, MiningService miningService, Socket socket) {
        this.node = node;
        this.blockChainService = blockChainService;
        this.miningService = miningService;
        this.socket = socket;
        this.dataMiddleware = new MessageMiddleware();
        try {
            this.out = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        this.running = true;
    }

    @Override
    public void run() {
        super.run();
        var mapper = new ObjectMapper();
        while (running) {
            InputStream in;
            try {
                in = socket.getInputStream();
            } catch (IOException e) {
    //            logSocketInfo("can't get input stream from socket");
                System.out.println("can't get input stream from socket");
                return;
            }
            try {
                if (in.available() > 0) {
                    var message = dataMiddleware.handle(in);
                    switch (message.getReqType()) {
                        case CONNECTED_NODES_REQUEST:
                            sendMessage(
                                    new Message(
                                            ReqType.CONNECTED_NODES_RESPONSE,
                                            mapper.writeValueAsBytes(node.getNodesAddresses())
                                    )
                            );
                            break;
                        case CONNECTED_NODES_RESPONSE:
                            var addresses = mapper.readValue(
                                    message.getContent(),
                                    new TypeReference<List<InetSocketAddress>>() {}
                            );
                            node.connectToNodes(addresses);
                            break;
                        case BLOCKCHAIN_REQUEST:
                            sendMessage(
                                    new Message(
                                            ReqType.BLOCKCHAIN_RESPONSE,
                                            blockChainService.getBlockchain().getBytes()
                                    )
                            );
                            break;
                        case BLOCKCHAIN_RESPONSE:
                            var blockchain = Blockchain.readFromInputStream(new ByteArrayInputStream(message.getContent()));
                            blockChainService.loadBlockChainToUTXOStorage(blockchain);
                            break;
                        case NEW_TRANSACTION:
                            var transaction = Transaction.readFromInputStream(new ByteArrayInputStream(message.getContent()));
                            miningService.registerNewTransaction(transaction);
                            break;
                        case NEW_BLOCK:
                            var block = Block.readFromInputStream(new ByteArrayInputStream(message.getContent()));
                            if(!blockChainService.addBlock(block)){
                                node.broadcast(new Message(ReqType.BLOCKCHAIN_REQUEST, new byte[]{}));
                            }
                            break;
                        case CLOSE_CONNECTION:
                            exit();
                            break;
                    }
//                    logSocketInfo("message received (" + message.getReqType().name() + ")");
                    System.out.println("message received (" + message.getReqType().name() + ")");
                }
            } catch (IOException | BlockDataException e) {
//                logSocketInfo("failed reading message from input stream");
                System.out.println("failed reading message from input stream");
                break;
            }
        }
        node.disconnectNode(this);
//        logSocketInfo("connection ended");
        System.out.println("connection ended");
    }

    public InetSocketAddress getSocketAddress() {
        return new InetSocketAddress(socket.getInetAddress(), socket.getPort());
    }

    public void exit() {
        try {
            running = false;
            socket.close();
        } catch (IOException ignored) {
//            logSocketInfo("couldn't close socket");
            System.out.println("couldn't close socket");
        }
    }

    public void sendMessage(Message message) {
        try {
            out.write(ArrayUtil.concat(
                    new ObjectMapper().writeValueAsBytes(message)
                    )
            );
        } catch (IOException e) {
//            logSocketInfo("cannot send message");
            System.out.println("cannot send message");
            exit();
        }
    }

    private void logSocketInfo(String info) {
        logger.info(socket.getInetAddress() + ":" + socket.getPort() + ": " + info);
    }

}
