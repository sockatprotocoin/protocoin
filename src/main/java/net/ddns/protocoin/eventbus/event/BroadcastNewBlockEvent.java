package net.ddns.protocoin.eventbus.event;

import net.ddns.protocoin.communication.data.Message;

public class BroadcastNewBlockEvent extends Event<Message> {
    public BroadcastNewBlockEvent(Message payload) {
        super(payload);
    }
}
