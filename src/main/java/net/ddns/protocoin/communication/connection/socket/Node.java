package net.ddns.protocoin.communication.connection.socket;

import net.ddns.protocoin.communication.data.Message;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class Node extends Thread {
    private final static Set<SocketThread> socketThreads = new CopyOnWriteArraySet<>();
    private final Logger logger = Logger.getLogger(Node.class.getName());

    public Node(InetSocketAddress initialNode) throws IOException {
        var socket = new Socket();
        socket.connect(initialNode, 1000);

    }

    public Node() {}

    @Override
    public void run() {
        super.run();
        ServerSocket serverSocket = null;
        Socket socket = null;
        final var port = 6969;

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
            var newSocketThread = new SocketThread(socket);
            socketThreads.add(newSocketThread);
            newSocketThread.start();
        }
    }

    public void broadcast(Message message) {
        socketThreads.forEach(socketThread -> socketThread.sendMessage(message));
    }
}
