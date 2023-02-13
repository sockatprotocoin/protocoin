package net.ddns.protocoin.communication.connection.socket;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.ddns.protocoin.communication.connection.DataMiddleware;
import net.ddns.protocoin.communication.data.Message;
import net.ddns.protocoin.core.blockchain.Blockchain;
import net.ddns.protocoin.core.blockchain.block.Block;
import net.ddns.protocoin.core.blockchain.block.BlockDataException;
import net.ddns.protocoin.core.blockchain.transaction.Transaction;
import net.ddns.protocoin.eventbus.EventBus;
import net.ddns.protocoin.eventbus.event.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SocketThread extends Thread {
    private static final Logger logger = Logger.getLogger(SocketThread.class.getName());
    private final OutputStream out;
    private final Socket socket;
    private final DataMiddleware<InputStream, Message> dataMiddleware;
    private final ObjectMapper objectMapper;
    private final EventBus eventBus;

    private boolean running;

    public SocketThread(
            Socket socket,
            DataMiddleware<InputStream, Message> dataMiddleware,
            ObjectMapper objectMapper,
            EventBus eventBus
    ) throws IOException {
        this.out = socket.getOutputStream();
        this.socket = socket;
        this.dataMiddleware = dataMiddleware;
        this.objectMapper = objectMapper;
        this.eventBus = eventBus;
        this.running = true;
    }

    @Override
    public void run() {
        super.run();
        logSocketInfo("connection opened with: " + socket.getInetAddress().getHostAddress() + ", on port: " + socket.getPort());
        while (running) {
            InputStream in;
            try {
                in = socket.getInputStream();
            } catch (IOException e) {
                logSocketInfo("can't get input stream from socket");
                return;
            }
            try {
                if (in.available() > 0) {
                    var message = dataMiddleware.handle(in);
                    logSocketInfo("message received (" + message.getReqType().name() + ")");
                    switch (message.getReqType()) {
                        case CONNECTED_NODES_REQUEST:
                            eventBus.postEvent(
                                    new ConnectedNodesRequestEvent(this::sendMessage)
                            );
                            break;
                        case CONNECTED_NODES_RESPONSE:
                            var addresses = objectMapper.readValue(
                                    message.getContent(),
                                    new TypeReference<List<InetSocketAddress>>() {}
                            );
                            eventBus.postEvent(new ConnectedNodesResponseEvent(addresses));
                            break;
                        case BLOCKCHAIN_REQUEST:
                            eventBus.postEvent(new BlockchainRequestEvent(this::sendMessage));
                            break;
                        case BLOCKCHAIN_RESPONSE:
                            var blockchain = Blockchain.readFromInputStream(new ByteArrayInputStream(message.getContent()));
                            eventBus.postEvent(new BlockchainResponseEvent(blockchain));
                            break;
                        case NEW_TRANSACTION:
                            var transaction = Transaction.readFromInputStream(new ByteArrayInputStream(message.getContent()));
                            eventBus.postEvent(new NewTransactionEvent(transaction));
                            break;
                        case NEW_BLOCK:
                            var block = Block.readFromInputStream(new ByteArrayInputStream(message.getContent()));
                            eventBus.postEvent(new NewBlockEvent(block));
                            break;
                        case CLOSE_CONNECTION:
                            exit();
                            break;
                    }
                }
            } catch (IOException | BlockDataException e) {
                logSocketInfo("failed reading message from input stream");
                break;
            }
        }
        eventBus.postEvent(new DisconnectNodeSocketEvent(this));
        logSocketInfo("connection ended");
    }

    public InetSocketAddress getSocketAddress() {
        return new InetSocketAddress(socket.getInetAddress(), socket.getPort());
    }

    public void exit() {
        try {
            running = false;
            socket.close();
        } catch (IOException ignored) {
            logSocketInfo("couldn't close socket");
        }
    }

    public void sendMessage(Message message) {
        try {
            logSocketInfo("message sending (" + message.getReqType().name() + ")");
            out.write(objectMapper.writeValueAsBytes(message));
        } catch (IOException e) {
            logSocketInfo("cannot send message");
            exit();
        }
    }

    private void logSocketInfo(String info) {
        logger.log(Level.INFO, socket.getInetAddress() + ":" + socket.getPort() + " " + info);
    }
}
