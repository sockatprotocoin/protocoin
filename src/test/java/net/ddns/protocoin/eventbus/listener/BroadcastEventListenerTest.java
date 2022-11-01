package net.ddns.protocoin.eventbus.listener;

import net.ddns.protocoin.communication.data.Message;
import net.ddns.protocoin.eventbus.event.BroadcastEvent;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class BroadcastEventListenerTest {
    @Test
    void shouldHandleBroadcastEvent() {
        final var messageToVerify = new Message[1];

        var messageMock = mock(Message.class);
        Consumer<Message> messageConsumer = msg -> messageToVerify[0] = msg;

        new BroadcastEventListener(messageConsumer).handle(new BroadcastEvent(messageMock));

        assertEquals(messageToVerify[0], messageMock);
    }
}