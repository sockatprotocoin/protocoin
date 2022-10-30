package net.ddns.protocoin.eventbus.listener;

import net.ddns.protocoin.eventbus.event.ConnectedNodesResponseEvent;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.function.Consumer;

public class ConnectedNodesResponseEventListener extends Listener<ConnectedNodesResponseEvent> {
    private final Consumer<List<InetSocketAddress>> nodeListConnector;

    public ConnectedNodesResponseEventListener(Consumer<List<InetSocketAddress>> nodeListConnector) {
        this.nodeListConnector = nodeListConnector;
    }

    @Override
    protected void handle(ConnectedNodesResponseEvent event) {
        nodeListConnector.accept(event.eventPayload());
    }
}
