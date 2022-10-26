package net.ddns.protocoin.connection;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.ddns.protocoin.communication.connection.MessageMiddleware;
import net.ddns.protocoin.communication.connection.socket.Node;
import net.ddns.protocoin.communication.data.Message;
import net.ddns.protocoin.communication.data.ReqType;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

class NodeTest {
    @Test
    void shouldServe() throws IOException {
        var node = new Node();
        node.start();

        var client = new Socket();
        client.connect(new InetSocketAddress("localhost", 6969), 1000);
//        var client2 = new Socket();
//        client2.connect(new InetSocketAddress("localhost", 6969), 1000);
//        var client3 = new Socket();
//        client3.connect(new InetSocketAddress("localhost", 6969), 1000);


//        var out = client.getOutputStream();
//        out.write("lol".getBytes());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        node.broadcast(new Message(ReqType.REGISTER_NEW_BLOCK, "lol"));
        var is = client.getInputStream();
        var msg = new MessageMiddleware().handle(is);
        var obj = new ObjectMapper().readValue(msg.getContent(), msg.getReqType().getType());
        client.close();
//        var out2 = client2.getOutputStream();
//        out2.write("lol".getBytes());
//        client2.close();
//        var out3 = client3.getOutputStream();
//        out3.write("lol".getBytes());
//        client3.close();
    }
}