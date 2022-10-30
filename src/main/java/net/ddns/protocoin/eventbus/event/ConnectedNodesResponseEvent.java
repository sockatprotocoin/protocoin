package net.ddns.protocoin.eventbus.event;

import java.net.InetSocketAddress;
import java.util.List;

public class ConnectedNodesResponseEvent extends Event<List<InetSocketAddress>> {
    public ConnectedNodesResponseEvent(List<InetSocketAddress> payload) {
        super(payload);
    }
}
