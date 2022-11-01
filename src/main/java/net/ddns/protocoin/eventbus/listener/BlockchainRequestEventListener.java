package net.ddns.protocoin.eventbus.listener;

import net.ddns.protocoin.communication.data.Message;
import net.ddns.protocoin.communication.data.ReqType;
import net.ddns.protocoin.core.blockchain.Blockchain;
import net.ddns.protocoin.eventbus.event.BlockchainRequestEvent;

import java.util.function.Supplier;

public class BlockchainRequestEventListener extends Listener<BlockchainRequestEvent> {
    private final Supplier<Blockchain> blockchainSupplier;

    public BlockchainRequestEventListener(Supplier<Blockchain> blockchainSupplier) {
        this.blockchainSupplier = blockchainSupplier;
    }

    @Override
    protected void handle(BlockchainRequestEvent event) {
        event.eventPayload().accept(
                new Message(
                        ReqType.BLOCKCHAIN_RESPONSE,
                        blockchainSupplier.get().getBytes()
                )
        );
    }
}
