package net.ddns.protocoin.connection;

import net.ddns.protocoin.communication.connection.socket.Node;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;

class NodeTest {
    @Test
    void shouldServe() throws IOException {
        var host1 = new InetSocketAddress("localhost", 6969);
        var host2 = new InetSocketAddress("localhost", 6970);
        var host3 = new InetSocketAddress("localhost", 6971);

        Node.startListening(6969);
        Node.startListening(6970); // będzie tworzył socket thread na porcie
        Node.startListening(6971);

        Node.connectToNode(host2);
//        var node = new Node();
//
//        var client = new Socket();
//        client.connect(new InetSocketAddress("localhost", 6969), 1000);
//        var client2 = new Socket();
//        client2.connect(new InetSocketAddress("localhost", 6969), 1000);
//        var client3 = new Socket();
//        client3.connect(new InetSocketAddress("localhost", 6969), 1000);


//        var out = client.getOutputStream();
//        out.write("lol".getBytes());
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        node.broadcast(new Message(ReqType.REGISTER_NEW_BLOCK, "lol"));
//        var is = client.getInputStream();
//        var msg = new MessageMiddleware().handle(is);
//        var obj = new ObjectMapper().readValue(msg.getContent(), msg.getReqType().getType());
//        client.close();
//        var out2 = client2.getOutputStream();
//        out2.write("lol".getBytes());
//        client2.close();
//        var out3 = client3.getOutputStream();
//        out3.write("lol".getBytes());
//        client3.close();
    }
}