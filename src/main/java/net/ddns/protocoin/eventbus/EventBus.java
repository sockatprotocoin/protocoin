package net.ddns.protocoin.eventbus;

import net.ddns.protocoin.eventbus.event.Event;
import net.ddns.protocoin.eventbus.listener.Listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventBus {
    Map<Class<? extends Event<?>>, List<Listener<?>>> listeners = new HashMap<>();

    public <E extends Event<?>, L extends Listener<E>> void registerListener(L listener) {
        var klass = listener.getEventType();
        if (!listeners.containsKey(klass)) {
            listeners.put(klass, new ArrayList<>());
        }
        listeners.get(klass).add(listener);
    }

    public <E extends Event<?>> void postEvent(E event) {
        listeners.get(event.getClass())
                 .forEach(listener -> listener.handleEvent(event));
    }
}
