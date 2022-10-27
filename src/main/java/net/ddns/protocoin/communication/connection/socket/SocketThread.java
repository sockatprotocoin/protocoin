package net.ddns.protocoin.communication.connection.socket;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.ddns.protocoin.communication.connection.DataMiddleware;
import net.ddns.protocoin.communication.connection.MessageMiddleware;
import net.ddns.protocoin.communication.data.Message;
import net.ddns.protocoin.communication.data.ReqType;
import net.ddns.protocoin.core.blockchain.Blockchain;
import net.ddns.protocoin.core.util.ArrayUtil;
import net.ddns.protocoin.service.BlockChainService;
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
    private final Socket socket;
    private final DataMiddleware<InputStream, Message> dataMiddleware;
    private OutputStream out;


    private boolean running;

    public SocketThread(Node node, BlockChainService blockChainService, Socket socket) {
        this.node = node;
        this.blockChainService = blockChainService;
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
        InputStream in;
        try {
            in = socket.getInputStream();
        } catch (IOException e) {
//            logSocketInfo("can't get input stream from socket");
            System.out.println("can't get input stream from socket");
            return;
        }
        while (running) {
            try {
                if (in.available() > 0) {
                    var message = dataMiddleware.handle(in);
                    switch (message.getReqType()) {
                        case ASK_FOR_CONNECTED_NODES:
                            sendMessage(
                                    new Message(
                                            ReqType.RETURN_CONNECTED_NODES,
                                            new ObjectMapper().writeValueAsBytes(node.getNodesAddresses())
                                    )
                            );
                            break;
                        case RETURN_CONNECTED_NODES:
                            var addresses = new ObjectMapper().readValue(
                                    message.getContent(),
                                    new TypeReference<List<InetSocketAddress>>() {}
                            );
                            node.connectToNodes(addresses);
                            break;
                        case ASK_FOR_BLOCKCHAIN:
                            sendMessage(
                                    new Message(
                                            ReqType.RETURN_BLOCKCHAIN,
                                            blockChainService.getBlockchain().getBytes()
                                    )
                            );
                            break;
                        case RETURN_BLOCKCHAIN:
                            var blockchain = Blockchain.readFromInputStream(new ByteArrayInputStream(message.getContent()));
                            blockChainService.loadBlockChainToUTXOStorage(blockchain);
                            break;
                        case CLOSE_CONNECTION:
                            exit();
                            break;
                    }
//                    logSocketInfo("message received (" + message.getReqType().name() + ")");
                    System.out.println("message received (" + message.getReqType().name() + ")");
                }
            } catch (IOException e) {
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
        }
    }

    private void logSocketInfo(String info) {
        logger.info(socket.getInetAddress() + ":" + socket.getPort() + ": " + info);
    }
}
