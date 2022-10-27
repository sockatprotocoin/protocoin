package net.ddns.protocoin.communication.connection.socket;

import net.ddns.protocoin.communication.data.Message;
import net.ddns.protocoin.communication.data.ReqType;
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
    private final static Set<SocketThread> socketThreads = new CopyOnWriteArraySet<>();
    private final static Logger logger = Logger.getLogger(Node.class.getName());

    public static void startListening(int port) {
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

    public static void connectToNodes(List<InetSocketAddress> inetSocketAddresses) throws IOException {
        for (InetSocketAddress inetSocketAddress : inetSocketAddresses) {
            connectToNode(inetSocketAddress);
        }
    }

    public static void connectToNode(InetSocketAddress inetSocketAddress) throws IOException {
        var socket = new Socket();
        socket.connect(inetSocketAddress, 1000);
        createThreadForConnection(socket);
    }

    private static void createThreadForConnection(Socket socket) {
        var newSocketThread = new SocketThread(socket);
        socketThreads.add(newSocketThread);
        newSocketThread.sendMessage(
                new Message(
                        ReqType.ASK_FOR_CONNECTED_NODES,
                        ""
                )
        );
        newSocketThread.start();
    }

    public static List<InetSocketAddress> getNodesAddresses() {
        return socketThreads.stream()
                .map(SocketThread::getSocketAddress)
                .collect(Collectors.toList());
    }

    public void broadcast(Message message) {
        socketThreads.forEach(socketThread -> socketThread.sendMessage(message));
    }
}
