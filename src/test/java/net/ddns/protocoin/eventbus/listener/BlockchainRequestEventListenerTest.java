package net.ddns.protocoin.eventbus.listener;

import net.ddns.protocoin.communication.data.Message;
import net.ddns.protocoin.communication.data.ReqType;
import net.ddns.protocoin.core.blockchain.Blockchain;
import net.ddns.protocoin.eventbus.event.BlockchainRequestEvent;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class BlockchainRequestEventListenerTest {
    @Test
    void shouldHandleBlockchainRequestEvent() {
        final var messageToVerify = new Message[1];
        final var bytesToVerify = new byte[0];

        var blockchain = mock(Blockchain.class);
        when(blockchain.getBytes()).thenReturn(bytesToVerify);
        Consumer<Message> messageDigest = message -> messageToVerify[0] = message;

        Supplier<Blockchain> blockchainSupplier = () -> blockchain;

        new BlockchainRequestEventListener(blockchainSupplier).handle(new BlockchainRequestEvent(messageDigest));
        assertNotNull(messageToVerify[0]);
        assertEquals(messageToVerify[0].getReqType(), ReqType.BLOCKCHAIN_RESPONSE);
        assertEquals(messageToVerify[0].getContent(), bytesToVerify);
    }
}