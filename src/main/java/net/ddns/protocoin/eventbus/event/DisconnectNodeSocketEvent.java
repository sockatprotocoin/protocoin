package net.ddns.protocoin.eventbus.event;

import net.ddns.protocoin.communication.connection.socket.SocketThread;

public class DisconnectNodeSocketEvent extends Event<SocketThread> {
    public DisconnectNodeSocketEvent(SocketThread payload) {
        super(payload);
    }
}
