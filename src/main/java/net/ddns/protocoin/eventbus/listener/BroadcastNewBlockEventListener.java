package net.ddns.protocoin.eventbus.listener;

import net.ddns.protocoin.communication.data.Message;
import net.ddns.protocoin.eventbus.event.BroadcastNewBlockEvent;

import java.util.function.Consumer;

public class BroadcastNewBlockEventListener extends Listener<BroadcastNewBlockEvent> {
    private final Consumer<Message> messageBroadcaster;

    public BroadcastNewBlockEventListener(Consumer<Message> messageBroadcaster) {
        this.messageBroadcaster = messageBroadcaster;
    }

    @Override
    protected void handle(BroadcastNewBlockEvent event) {
        messageBroadcaster.accept(event.eventPayload());
    }
}
