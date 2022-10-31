package net.ddns.protocoin.eventbus.listener;

import net.ddns.protocoin.communication.data.Message;
import net.ddns.protocoin.eventbus.event.BroadcastEvent;

import java.util.function.Consumer;

public class BroadcastEventListener extends Listener<BroadcastEvent> {
    private final Consumer<Message> messageBroadcaster;

    public BroadcastEventListener(Consumer<Message> messageBroadcaster) {
        this.messageBroadcaster = messageBroadcaster;
    }

    @Override
    protected void handle(BroadcastEvent event) {
        messageBroadcaster.accept(event.eventPayload());
    }
}
