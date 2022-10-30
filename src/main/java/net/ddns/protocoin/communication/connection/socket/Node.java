package net.ddns.protocoin.communication.connection.socket;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.ddns.protocoin.communication.connection.MessageMiddleware;
import net.ddns.protocoin.communication.data.Message;
import net.ddns.protocoin.communication.data.ReqType;
import net.ddns.protocoin.eventbus.EventBus;
import net.ddns.protocoin.eventbus.listener.BroadcastNewBlockEventListener;
import net.ddns.protocoin.eventbus.listener.ConnectedNodesRequestEventListener;
import net.ddns.protocoin.eventbus.listener.ConnectedNodesResponseEventListener;
import net.ddns.protocoin.eventbus.listener.DisconnectNodeSocketEventListener;
import net.ddns.protocoin.service.MiningService;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

public class Node {
    private final static Logger logger = Logger.getLogger(Node.class.getName());
    private final Set<SocketThread> socketThreads = new CopyOnWriteArraySet<>();
    private final EventBus eventBus;
    private final MiningService miningService;

    public Node(
            MiningService miningService,
            EventBus eventBus
    ) {
        this.eventBus = eventBus;
        this.miningService = miningService;
        setupListeners();
    }

    private void setupListeners() {
        eventBus.registerListener(new ConnectedNodesRequestEventListener(new ObjectMapper(), this::getNodesAddresses));
        eventBus.registerListener(new DisconnectNodeSocketEventListener(this::disconnectNode));
        eventBus.registerListener(new ConnectedNodesResponseEventListener(this::connectToNodes));
        eventBus.registerListener(new BroadcastNewBlockEventListener(this::broadcast));
    }

    public void startMining() {
        new Thread(() -> {
            while (true) {
                if (miningService.getNumberOfWaitingTransactions() >= 2) {
                    var newBlock = miningService.startMining();
                    broadcast(new Message(ReqType.NEW_BLOCK, newBlock.getBytes()));
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    public void startListening(int port) {
        new Thread(() -> {
            ServerSocket serverSocket = null;
            Socket socket = null;
            try {
                serverSocket = new ServerSocket(port);
            } catch (IOException e) {
                logger.error("Cannot create socket server on port: " + port);
            }

            while (true) {
                try {
                    socket = serverSocket.accept();
                } catch (IOException e) {
                    logger.error("Failed reading on port: " + port);
                }
                createThreadForConnection(socket);
            }
        }).start();
    }

    public void connectToNodes(List<InetSocketAddress> inetSocketAddresses) {
        for (InetSocketAddress inetSocketAddress : inetSocketAddresses) {
            try {
                connectToNode(inetSocketAddress);
            } catch (IOException e) {
                System.out.println("Cannot connect to node: " + inetSocketAddress.getAddress().getHostAddress());
            }
        }
    }

    public void connectToNode(InetSocketAddress inetSocketAddress) throws IOException {
        var socket = new Socket();
        socket.connect(inetSocketAddress, 1000);
        socket.getOutputStream().write(new ObjectMapper().writeValueAsBytes(
                new Message(
                        ReqType.CONNECTED_NODES_REQUEST,
                        new byte[0]
                )
        ));
        createThreadForConnection(socket);
    }

    public void disconnectNode(SocketThread socketThread) {
        socketThreads.remove(socketThread);
    }

    private void createThreadForConnection(Socket socket) {
        try {
            var newSocketThread = new SocketThread(socket, new MessageMiddleware(), new ObjectMapper(), eventBus);
            socketThreads.add(newSocketThread);
            newSocketThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<InetSocketAddress> getNodesAddresses() {
        return socketThreads.stream()
                .map(SocketThread::getSocketAddress)
                .collect(Collectors.toList());
    }

    public void broadcast(Message message) {
        socketThreads.forEach(socketThread -> socketThread.sendMessage(message));
        System.out.println("MSG SENT");
    }
}
