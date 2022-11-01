package net.ddns.protocoin.eventbus.event;

public abstract class Event<T> {
    private final T payload;

    public Event(T payload) {
        this.payload = payload;
    }

    public T eventPayload() {
        return payload;
    }
}
