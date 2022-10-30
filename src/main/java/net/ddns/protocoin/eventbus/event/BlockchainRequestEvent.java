package net.ddns.protocoin.eventbus.event;

import net.ddns.protocoin.communication.data.Message;

import java.util.function.Consumer;

public class BlockchainRequestEvent extends Event<Consumer<Message>> {
    public BlockchainRequestEvent(Consumer<Message> messageSender) {
        super(messageSender);
    }
}
