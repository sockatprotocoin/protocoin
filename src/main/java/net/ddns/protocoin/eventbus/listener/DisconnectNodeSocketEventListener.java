package net.ddns.protocoin.eventbus.listener;

import net.ddns.protocoin.communication.connection.socket.SocketThread;
import net.ddns.protocoin.eventbus.event.DisconnectNodeSocketEvent;

import java.util.function.Consumer;

public class DisconnectNodeSocketEventListener extends Listener<DisconnectNodeSocketEvent> {
    private final Consumer<SocketThread> nodeDisconnecter;

    public DisconnectNodeSocketEventListener(Consumer<SocketThread> nodeDisconnecter) {
        this.nodeDisconnecter = nodeDisconnecter;
    }

    @Override
    protected void handle(DisconnectNodeSocketEvent event) {
        nodeDisconnecter.accept(event.eventPayload());
    }
}
