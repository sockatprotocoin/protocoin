package net.ddns.protocoin.communication.connection.socket;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.ddns.protocoin.communication.connection.MessageMiddleware;
import net.ddns.protocoin.communication.data.Message;
import net.ddns.protocoin.communication.data.ReqType;
import net.ddns.protocoin.eventbus.EventBus;
import net.ddns.protocoin.eventbus.event.NewBlockEvent;
import net.ddns.protocoin.eventbus.listener.BroadcastEventListener;
import net.ddns.protocoin.eventbus.listener.ConnectedNodesRequestEventListener;
import net.ddns.protocoin.eventbus.listener.ConnectedNodesResponseEventListener;
import net.ddns.protocoin.eventbus.listener.DisconnectNodeSocketEventListener;
import net.ddns.protocoin.service.MiningService;

import java.io.IOException;
import java.net.*;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Node {
    private final Logger logger = Logger.getLogger(Node.class.getName());
    private final Set<SocketThread> socketThreads = new CopyOnWriteArraySet<>();
    private final EventBus eventBus;
    private final MiningService miningService;
    private final int port;
    private final String machineIp;

    public Node(
            MiningService miningService,
            EventBus eventBus,
            int port) {
        this.eventBus = eventBus;
        this.miningService = miningService;
        this.port = port;
        this.machineIp = findMachineIp();
        setupListeners();
    }

    private void setupListeners() {
        eventBus.registerListener(new ConnectedNodesRequestEventListener(new ObjectMapper(), this::getNodesAddresses));
        eventBus.registerListener(new DisconnectNodeSocketEventListener(this::disconnectNode));
        eventBus.registerListener(new ConnectedNodesResponseEventListener(this::connectToNodes));
        eventBus.registerListener(new BroadcastEventListener(this::broadcast));
    }

    public void startMining(int transactionNeededForNewBlock) {
        new Thread(() -> {
            while (true) {
                if (miningService.getNumberOfWaitingTransactions() >= transactionNeededForNewBlock) {
                    var newBlock = miningService.startMining();
                    eventBus.postEvent(new NewBlockEvent(newBlock));
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
        logger.log(Level.INFO, "Mining process started");
    }

    public void startListening() {
        logger.log(Level.INFO, "Starting to listen for new connections on port: " + port);
        new Thread(() -> {
            ServerSocket serverSocket = null;
            Socket socket = null;
            try {
                serverSocket = new ServerSocket(port);
            } catch (IOException e) {
                logger.log(Level.WARNING, "Cannot create socket server on port: " + port);
            }

            while (true) {
                try {
                    socket = serverSocket.accept();
                    createThreadForConnection(socket);
                } catch (IOException e) {
                    logger.log(Level.WARNING, "Failed reading on port: " + port);
                }
            }
        }).start();
    }

    private String findMachineIp() {
        try {
            var enumeration = NetworkInterface.getNetworkInterfaces();
            while (enumeration.hasMoreElements()) {
                var networkInterface = enumeration.nextElement();
                var interfaceName = networkInterface.getName();
                if (interfaceName.equals("en0") || interfaceName.equals("eth0")) {
                    for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                        var address = interfaceAddress.getAddress();
                        if (address instanceof Inet4Address) {
                            logger.log(Level.INFO, "Local machine address: " + address.getHostAddress());
                            return address.getHostAddress();
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void connectToNodes(List<InetSocketAddress> inetSocketAddresses) {
        inetSocketAddresses.forEach(inetSocketAddress -> {
            try {
                connectToNode(inetSocketAddress);
            } catch (IOException e) {
                logger.log(Level.WARNING, "Cannot connect to node: " + inetSocketAddress.getAddress().getHostAddress());
            }
        });
    }

    public void connectToNode(InetSocketAddress inetSocketAddress) throws IOException {
        if (
                socketThreads.stream().anyMatch(socketThread ->
                        socketThread.getSocketAddress().getAddress().getHostAddress().equals(
                                inetSocketAddress.getAddress().getHostAddress()
                        ) || inetSocketAddress.getAddress().getHostAddress().equals(machineIp)
                )
        ) {
            return;
        }
        var socket = new Socket();
        socket.connect(inetSocketAddress, 1000);
        createThreadForConnection(socket);
    }

    public void disconnectNode(SocketThread socketThread) {
        socketThreads.remove(socketThread);
    }

    private void createThreadForConnection(Socket socket) throws IOException {
        var newSocketThread = new SocketThread(socket, new MessageMiddleware(), new ObjectMapper(), eventBus);
        socketThreads.add(newSocketThread);
        newSocketThread.setOnThreadStarted(() -> {
            newSocketThread.sendMessage(
                    new Message(
                            ReqType.CONNECTED_NODES_REQUEST, new byte[0]
                    )
            );
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {}
            newSocketThread.sendMessage(
                    new Message(
                            ReqType.BLOCKCHAIN_REQUEST, new byte[0]
                    )
            );
        });
        newSocketThread.start();
    }

    public List<InetSocketAddress> getNodesAddresses() {
        return socketThreads.stream()
                .map(SocketThread::getSocketAddress)
                .map(inetSocketAddress -> new InetSocketAddress(inetSocketAddress.getAddress(), port))
                .collect(Collectors.toList());
    }

    public void broadcast(Message message) {
        socketThreads.forEach(socketThread -> socketThread.sendMessage(message));
        logger.log(Level.INFO, "message broadcasted to network");
    }

    public int connectedNetworkSize() {
        return socketThreads.size();
    }

    public int getPort() {
        return port;
    }
}
