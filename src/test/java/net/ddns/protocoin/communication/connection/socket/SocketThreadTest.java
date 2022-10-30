package net.ddns.protocoin.communication.connection.socket;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.ddns.protocoin.communication.connection.MessageMiddleware;
import net.ddns.protocoin.core.util.Converter;
import net.ddns.protocoin.eventbus.EventBus;
import net.ddns.protocoin.eventbus.event.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class SocketThreadTest {
    private final static byte[] CLOSE_CONNECTION_BYTES = Converter.hexStringToByteArray("7B2272657154797065223A22434C4F53455F434F4E4E454354494F4E222C22636F6E74656E74223A22227D");
    private final static byte[] CONNECTED_NODES_REQUEST_BYTES = Converter.hexStringToByteArray("7B2272657154797065223A22434F4E4E45435445445F4E4F4445535F52455155455354222C22636F6E74656E74223A22227D");
    private final static byte[] CONNECTED_NODES_RESPONSE_BYTES = Converter.hexStringToByteArray("7B2272657154797065223A22434F4E4E45435445445F4E4F4445535F524553504F4E5345222C22636F6E74656E74223A22227D");
    private final static byte[] BLOCKCHAIN_REQUEST_BYTES = Converter.hexStringToByteArray("7B2272657154797065223A22424C4F434B434841494E5F52455155455354222C22636F6E74656E74223A22227D");
    private final static byte[] BLOCKCHAIN_RESPONSE_BYTES = Converter.hexStringToByteArray("7B2272657154797065223A22424C4F434B434841494E5F524553504F4E5345222C22636F6E74656E74223A22227D");
    private final static byte[] NEW_TRANSACTION_BYTES = Converter.hexStringToByteArray("7B2272657154797065223A224E45575F5452414E53414354494F4E222C22636F6E74656E74223A2241684A545A484F436B514B44456C4E6B63344B52416F4D535532527A6770454367784A545A484F436B514B442F2F2F2F2F3252344F514936764E52344F5343727A526735416B685764595354416A6C495634535441674F5568585A59535443727A6553446B674F5568585A595354416A6C4956345354416A6C4956345354416A6C49563453546C6B65446B434F727A5565446B67713830594F514A49566E57456B774935534665456B7749446C49563257456B777138336B673549446C49563257456B774935534665456B774935534665456B774935534665456B35456C4E6B63344B52416F4D535532527A6770454367784A545A484F436B514B44456C4E6B63344B52416F502F2F2F2F2F5A486735416A713831486735494B764E47446B4353465A31684A4D434F556858684A4D4341355346646C684A4D4B764E35494F5341355346646C684A4D434F556858684A4D434F556858684A4D434F556858684A4F5752344F514936764E52344F5343727A526735416B685764595354416A6C495634535441674F5568585A59535443727A6553446B674F5568585A595354416A6C4956345354416A6C4956345354416A6C495634535541434141414141536F4638674159415149304F356A6C3849594C5169317869506E33644F66476C622F4172774D464141414141536F4638674159415149304F356A6C3849594C5169317869506E33644F66476C622F4172774968227D");
    private final static byte[] NEW_BLOCK_BYTES = Converter.hexStringToByteArray("7B2272657154797065223A224E45575F424C4F434B222C22636F6E74656E74223A2242676B4743514B4B6351344F4E71695349392B677332334D315A2F743165793355754E526F4679795A5052646D4E5969414141414141414141414141414141414141414141414141414141414141414141414141414141414141426A586F54514941615739463142574177424166716572775358544339614A4E4535526D726B724E652F5746396841563373417A393345663730666867534141414141486D2B5A6E3735334C7573566142696C633648437763436D2F7A624C63346F32566E79675673572B42655962766A79326A3346372F314A3039524469396252496177656146476F484D346E4E5853525A6F534D4C657075462F59665767587843696E505873746A67476E356F4E73506C3355394247363762715151566674544675662B416E755A4E4B616A4847587055554B364A6E6A70424670473859735333494141397A33737A794B3341514141414145714266494147514543464E44733963446338336C794154656531493874343341432F6F49384177553D227D");

    private Socket socketMock;
    private ObjectMapper objectMapperMock;
    private EventBus eventBusMock;

    @BeforeEach
    void setup() {
        socketMock = Mockito.mock(Socket.class);
        objectMapperMock = Mockito.mock(ObjectMapper.class);
        eventBusMock = Mockito.mock(EventBus.class);
    }

    @Test
    void shouldCloseConnection() throws IOException, InterruptedException {
        // given:
        when(socketMock.getInputStream()).thenReturn(new ByteArrayInputStream(CLOSE_CONNECTION_BYTES));

        // when:
        var socketThread = new SocketThread(socketMock, new MessageMiddleware(), objectMapperMock, eventBusMock);
        socketThread.start();

        // then:
        socketThread.join(1000);
        verify(eventBusMock, times(1)).postEvent(any(DisconnectNodeSocketEvent.class));
        verifyNoMoreInteractions(eventBusMock);
        verify(socketMock, times(1)).close();
    }

    @Test
    void shouldAnswerConnectedNodesResponse() throws IOException, InterruptedException {
        // given:
        when(socketMock.getInputStream()).thenReturn(
                new ByteArrayInputStream(CONNECTED_NODES_REQUEST_BYTES),
                new ByteArrayInputStream(CONNECTED_NODES_RESPONSE_BYTES),
                new ByteArrayInputStream(BLOCKCHAIN_REQUEST_BYTES),
                new ByteArrayInputStream(BLOCKCHAIN_RESPONSE_BYTES),
                new ByteArrayInputStream(NEW_TRANSACTION_BYTES),
                new ByteArrayInputStream(NEW_BLOCK_BYTES),
                new ByteArrayInputStream(CLOSE_CONNECTION_BYTES)
        );

        // when:
        var socketThread = new SocketThread(socketMock, new MessageMiddleware(), objectMapperMock, eventBusMock);
        socketThread.start();

        // then:
        socketThread.join(1000);
        var eventArgumentCaptor = ArgumentCaptor.forClass(Event.class);

        verify(eventBusMock, times(7)).postEvent(eventArgumentCaptor.capture());
        assertEquals(eventArgumentCaptor.getAllValues().get(0).getClass(), ConnectedNodesRequestEvent.class);
        assertEquals(eventArgumentCaptor.getAllValues().get(1).getClass(), ConnectedNodesResponseEvent.class);
        assertEquals(eventArgumentCaptor.getAllValues().get(2).getClass(), BlockchainRequestEvent.class);
        assertEquals(eventArgumentCaptor.getAllValues().get(3).getClass(), BlockchainResponseEvent.class);
        assertEquals(eventArgumentCaptor.getAllValues().get(4).getClass(), NewTransactionEvent.class);
        assertEquals(eventArgumentCaptor.getAllValues().get(5).getClass(), NewBlockEvent.class);
        assertEquals(eventArgumentCaptor.getAllValues().get(6).getClass(), DisconnectNodeSocketEvent.class);
        verifyNoMoreInteractions(eventBusMock);
        verify(socketMock, times(1)).close();
    }
}