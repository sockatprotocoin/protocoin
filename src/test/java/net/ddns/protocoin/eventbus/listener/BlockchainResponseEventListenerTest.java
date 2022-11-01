package net.ddns.protocoin.eventbus.listener;

import net.ddns.protocoin.core.blockchain.Blockchain;
import net.ddns.protocoin.eventbus.event.BlockchainResponseEvent;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class BlockchainResponseEventListenerTest {
    @Test
    void shouldHandleBlockchainRequestEvent() {
        final var blockchainToVerify = new Blockchain[1];

        var blockchain = mock(Blockchain.class);
        Consumer<Blockchain> blockchainDigest = blk -> blockchainToVerify[0] = blk;

        new BlockchainResponseEventListener(blockchainDigest).handle(new BlockchainResponseEvent(blockchain));

        assertEquals(blockchainToVerify[0], blockchain);
    }
}