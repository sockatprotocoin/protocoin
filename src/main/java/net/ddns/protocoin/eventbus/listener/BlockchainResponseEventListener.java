package net.ddns.protocoin.eventbus.listener;

import net.ddns.protocoin.eventbus.event.BlockchainResponseEvent;
import net.ddns.protocoin.core.blockchain.Blockchain;

import java.util.function.Consumer;

public class BlockchainResponseEventListener extends Listener<BlockchainResponseEvent> {
    private final Consumer<Blockchain> blockchainLoader;

    public BlockchainResponseEventListener(Consumer<Blockchain> blockchainLoader) {
        this.blockchainLoader = blockchainLoader;
    }

    @Override
    protected void handle(BlockchainResponseEvent event) {
        blockchainLoader.accept(event.eventPayload());
    }
}
