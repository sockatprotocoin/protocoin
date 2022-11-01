package net.ddns.protocoin.eventbus.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.ddns.protocoin.communication.data.Message;
import net.ddns.protocoin.communication.data.ReqType;
import net.ddns.protocoin.eventbus.event.ConnectedNodesRequestEvent;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ConnectedNodesRequestEventListenerTest {
    @Test
    void shouldHandleConnectedNodesRequestEvent() throws JsonProcessingException {
        final var messageToVerify = new Message[1];
        var bytesToVerify = new byte[0];
        var objectMapperMock = mock(ObjectMapper.class);
        when(objectMapperMock.writeValueAsBytes(any(List.class))).thenReturn(bytesToVerify);
        Consumer<Message> messageSender = msg -> messageToVerify[0] = msg;
        var nodesAddressesList = new ArrayList<InetSocketAddress>();
        Supplier<List<InetSocketAddress>> nodesAddressesSupplier = () -> nodesAddressesList;

        new ConnectedNodesRequestEventListener(objectMapperMock, nodesAddressesSupplier).handle(new ConnectedNodesRequestEvent(messageSender));

        assertNotNull(messageToVerify[0]);
        assertEquals(ReqType.CONNECTED_NODES_RESPONSE, messageToVerify[0].getReqType());
        assertEquals(bytesToVerify, messageToVerify[0].getContent());
    }
}