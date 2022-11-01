package net.ddns.protocoin.eventbus.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.ddns.protocoin.eventbus.event.ConnectedNodesResponseEvent;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ConnectedNodesResponseEventListenerTest {
    @Test
    void shouldHandleConnectedNodesResponseEvent() throws JsonProcessingException {
        final List<InetSocketAddress>[] listToVerify = new List[]{null};
        var bytesToVerify = new byte[0];
        var objectMapperMock = mock(ObjectMapper.class);
        when(objectMapperMock.writeValueAsBytes(any(List.class))).thenReturn(bytesToVerify);
        var nodesAddressesList = new ArrayList<InetSocketAddress>();
        Consumer<List<InetSocketAddress>> nodesAddressesConsumer = addressList -> listToVerify[0] = addressList;

        new ConnectedNodesResponseEventListener(nodesAddressesConsumer)
                .handle(new ConnectedNodesResponseEvent(nodesAddressesList));

        assertEquals(nodesAddressesList, listToVerify[0]);
    }
}