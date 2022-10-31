package net.ddns.protocoin.eventbus.listener;

import net.ddns.protocoin.core.blockchain.block.Block;
import net.ddns.protocoin.eventbus.event.NewBlockEvent;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class NewBlockEventListenerTest {
    @Test
    void shouldHandleNewBlockEvent() {
        final var blockToVerify = new Block[1];
        var block = mock(Block.class);
        Consumer<Block> blockConsumer = blk ->  blockToVerify[0] = blk;

        new NewBlockEventListener(blockConsumer).handle(new NewBlockEvent(block));

        assertEquals(blockToVerify[0], block);
    }
}