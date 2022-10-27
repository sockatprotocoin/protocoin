package net.ddns.protocoin.communication.connection.socket;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.ddns.protocoin.communication.connection.DataMiddleware;
import net.ddns.protocoin.communication.connection.MessageMiddleware;
import net.ddns.protocoin.communication.data.Message;
import net.ddns.protocoin.communication.data.ReqType;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

public class SocketThread extends Thread {
    private final Logger logger = Logger.getLogger(SocketThread.class.getName());
    private final Socket socket;
    private final DataMiddleware<InputStream, Message> dataMiddleware;
    private BufferedReader in;
    private PrintWriter out;

    private boolean running;

    public SocketThread(Socket socket) {
        this.socket = socket;
        this.dataMiddleware = new MessageMiddleware();
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            return;
        }
        this.running = true;
    }

    @Override
    public void run() {
        super.run();
        while (running) {
            try {
                if (in.ready()) {
                    var message = dataMiddleware.handle(new ByteArrayInputStream(in.readLine().getBytes()));
                    switch (message.getReqType()) {
                        case ASK_FOR_CONNECTED_NODES:
                            System.out.println("ASK_FOR_CONNECTED_NODES");
                            sendMessage(new Message(
                                    ReqType.RETURN_CONNECTED_NODES,
                                    new ObjectMapper().writeValueAsString(Node.getNodesAddresses())
                                    )
                            );
                            break;
                        case RETURN_CONNECTED_NODES:
                            var addresses = new ObjectMapper().readValue(
                                    message.getContent(),
                                    new TypeReference<List<InetSocketAddress>>() {}
                            );
                            Node.connectToNodes(addresses);
                            break;
                        case CLOSE_CONNECTION:
                            exit();
                            break;
                    }
                    System.out.println(message.getReqType().name());
//                    logSocketInfo("message received (" + message.getReqType().name() + ")");
                }
            } catch (IOException e) {
                System.out.println("failed reading message from input stream");
                break;
            }
        }
        System.out.println("thread ended");
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
            out.write(new ObjectMapper().writeValueAsString(message));
            System.out.println("sended message");
        } catch (IOException e) {
            logSocketInfo("cannot send message");
        }
    }

    private void logSocketInfo(String info) {
        logger.info(socket.getInetAddress() + ":" + socket.getPort() + ": " + info);
    }
}
