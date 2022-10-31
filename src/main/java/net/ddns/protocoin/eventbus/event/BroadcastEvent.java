package net.ddns.protocoin.eventbus.event;

import net.ddns.protocoin.communication.data.Message;

public class BroadcastEvent extends Event<Message> {
    public BroadcastEvent(Message payload) {
        super(payload);
    }
}
