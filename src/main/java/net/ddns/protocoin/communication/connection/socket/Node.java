package net.ddns.protocoin.communication.connection.socket;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.ddns.protocoin.communication.data.Message;
import net.ddns.protocoin.communication.data.ReqType;
import net.ddns.protocoin.service.BlockChainService;
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
    private final BlockChainService blockChainService;
    private final MiningService miningService;

    public Node(BlockChainService blockChainService, MiningService miningService) {
        this.blockChainService = blockChainService;
        this.miningService = miningService;
    }
    public void startMining() {
        new Thread(() -> {
            while (true) {
                if(miningService.getNumberOfWaitingTransactions() >=2){
                    var newBlock = miningService.startMining();
                    broadcast(new Message(ReqType.NEW_BLOCK,newBlock.getBytes()));
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
        var newSocketThread = new SocketThread(this, blockChainService, miningService, socket);
        socketThreads.add(newSocketThread);
        newSocketThread.start();
    }

    public List<InetSocketAddress> getNodesAddresses() {
        return socketThreads.stream()
                .map(SocketThread::getSocketAddress)
                .collect(Collectors.toList());
    }

    public void broadcast(Message message) {
        socketThreads.forEach(socketThread -> socketThread.sendMessage(message));
    }
}
