package net.ddns.protocoin.eventbus.event;

import net.ddns.protocoin.core.blockchain.block.Block;

public class NewBlockEvent extends Event<Block> {
    public NewBlockEvent(Block payload) {
        super(payload);
    }
}
