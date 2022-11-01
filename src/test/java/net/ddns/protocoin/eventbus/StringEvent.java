package net.ddns.protocoin.eventbus;

import net.ddns.protocoin.eventbus.event.Event;

public class StringEvent extends Event<String> {
    protected StringEvent(String payload) {
        super(payload);
    }
}
