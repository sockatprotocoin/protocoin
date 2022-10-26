package net.ddns.protocoin.communication.connection.socket;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.ddns.protocoin.communication.connection.DataHandler;
import net.ddns.protocoin.communication.connection.DataMiddleware;
import net.ddns.protocoin.communication.connection.MessageHandler;
import net.ddns.protocoin.communication.connection.MessageMiddleware;
import net.ddns.protocoin.communication.data.Message;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class SocketThread extends Thread {
    private final Logger logger = Logger.getLogger(SocketThread.class.getName());
    private final Socket socket;
    private final DataMiddleware<InputStream, Message> dataMiddleware;
    private final DataHandler<Message> messageHandler;

    public SocketThread(Socket socket) {
        this.socket = socket;
        this.dataMiddleware = new MessageMiddleware();
        this.messageHandler = new MessageHandler();
    }

    @Override
    public void run() {
        super.run();
        while (messageHandler.connectionOpened()) {
            InputStream inputStream;
            try {
                inputStream = socket.getInputStream();
            } catch (IOException e) {
                logger.info("connection closed unexpectedly");
                try {
                    socket.close();
                } catch (IOException ignored) {
                }
                return;
            }
            try {
                if (inputStream.available() > 0) {
                    var message = dataMiddleware.handle(inputStream);
                    messageHandler.handleData(message);
                    logSocketInfo("message received (" + message + ")");
                }
            } catch (IOException e) {
                logger.info("failed reading message from input stream");
            }
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
