package net.ddns.protocoin.eventbus;

import net.ddns.protocoin.eventbus.event.Event;

public class IntegerEvent extends Event<Integer> {
    protected IntegerEvent(Integer payload) {
        super(payload);
    }
}
