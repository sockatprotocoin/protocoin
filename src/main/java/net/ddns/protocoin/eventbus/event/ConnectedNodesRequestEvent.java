package net.ddns.protocoin.eventbus.event;

import net.ddns.protocoin.communication.data.Message;

import java.util.function.Consumer;

public class ConnectedNodesRequestEvent extends Event<Consumer<Message>> {
    public ConnectedNodesRequestEvent(Consumer<Message> messageSender) {
        super(messageSender);
    }
}
