package net.ddns.protocoin.eventbus.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.ddns.protocoin.communication.data.Message;
import net.ddns.protocoin.communication.data.ReqType;
import net.ddns.protocoin.eventbus.event.ConnectedNodesRequestEvent;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.function.Supplier;

public class ConnectedNodesRequestEventListener extends Listener<ConnectedNodesRequestEvent> {
    private final ObjectMapper objectMapper;
    private final Supplier<List<InetSocketAddress>> nodesAddressesSupplier;

    public ConnectedNodesRequestEventListener(ObjectMapper objectMapper, Supplier<List<InetSocketAddress>> nodesAddressesSupplier) {
        this.objectMapper = objectMapper;
        this.nodesAddressesSupplier = nodesAddressesSupplier;
    }

    @Override
    protected void handle(ConnectedNodesRequestEvent event) {
        try {
            event.eventPayload().accept(new Message(
                    ReqType.CONNECTED_NODES_RESPONSE,
                    objectMapper.writeValueAsBytes(nodesAddressesSupplier.get())
            ));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
