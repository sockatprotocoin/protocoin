package net.ddns.protocoin.eventbus.listener;

import net.ddns.protocoin.communication.connection.socket.SocketThread;
import net.ddns.protocoin.eventbus.event.DisconnectNodeSocketEvent;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class DisconnectNodeSocketEventListenerTest {
    @Test
    void shouldHandleDisconnectNodeSocketEvent() {
        final var socketThreadToVerify = new SocketThread[1];
        var socketThreadMock = mock(SocketThread.class);
        Consumer<SocketThread> nodeDisconnecter = socketThread -> socketThreadToVerify[0] = socketThread;

        new DisconnectNodeSocketEventListener(nodeDisconnecter).handle(new DisconnectNodeSocketEvent(socketThreadMock));

        assertEquals(socketThreadMock, socketThreadToVerify[0]);
    }
}