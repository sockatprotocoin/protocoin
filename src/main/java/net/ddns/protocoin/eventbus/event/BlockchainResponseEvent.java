package net.ddns.protocoin.eventbus.event;

import net.ddns.protocoin.core.blockchain.Blockchain;

public class BlockchainResponseEvent extends Event<Blockchain> {
    public BlockchainResponseEvent(Blockchain payload) {
        super(payload);
    }
}
