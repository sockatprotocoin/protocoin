package net.ddns.protocoin.communication.connection.socket;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.ddns.protocoin.communication.connection.DataMiddleware;
import net.ddns.protocoin.communication.connection.MessageMiddleware;
import net.ddns.protocoin.communication.data.Message;
import net.ddns.protocoin.communication.data.ReqType;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

public class SocketThread extends Thread {
    private final Logger logger = Logger.getLogger(SocketThread.class.getName());
    private final Socket socket;
    private final DataMiddleware<InputStream, Message> dataMiddleware;
    private boolean running;

    public SocketThread(Socket socket) {
        this.socket = socket;
        this.dataMiddleware = new MessageMiddleware();
        this.running = true;
    }

    @Override
    public void run() {
        super.run();
        while (running) {
            InputStream inputStream;
            try {
                inputStream = socket.getInputStream();
            } catch (IOException e) {
                logSocketInfo("connection closed unexpectedly");
                exit();
                break;
            }
            try {
                if (inputStream.available() > 0) {
                    var message = dataMiddleware.handle(inputStream);
                    switch (message.getReqType()) {
                        case ASK_FOR_CONNECTED_NODES:
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
                    logSocketInfo("message received (" + message + ")");
                }
            } catch (IOException e) {
                logSocketInfo("failed reading message from input stream");
            }
        }
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
            socket.getOutputStream().write(new ObjectMapper().writeValueAsBytes(message));
        } catch (IOException e) {
            logSocketInfo("cannot send message");
        }
    }

    private void logSocketInfo(String info) {
        logger.info(socket.getInetAddress() + ":" + socket.getPort() + ": " + info);
    }
}
