package net.ddns.protocoin.eventbus.listener;

import net.ddns.protocoin.eventbus.event.Event;

import java.lang.reflect.ParameterizedType;

public abstract class Listener<E extends Event<?>> {
    public Class<E> getEventType() {
        return (Class<E>)((ParameterizedType)getClass()
                .getGenericSuperclass())
                .getActualTypeArguments()[0];
    }

    public void handleEvent(Event event) {
        handle(getEventType().cast(event));
    }

    protected abstract void handle(E event);
}
