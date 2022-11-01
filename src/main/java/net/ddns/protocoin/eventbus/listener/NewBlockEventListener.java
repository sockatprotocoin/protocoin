package net.ddns.protocoin.eventbus.listener;

import net.ddns.protocoin.core.blockchain.block.Block;
import net.ddns.protocoin.eventbus.event.NewBlockEvent;

import java.util.function.Consumer;

public class NewBlockEventListener extends Listener<NewBlockEvent> {
    private final Consumer<Block> blockAdder;

    public NewBlockEventListener(Consumer<Block> blockAdder) {
        this.blockAdder = blockAdder;
    }

    @Override
    protected void handle(NewBlockEvent event) {
        blockAdder.accept(event.eventPayload());
    }
}
