package net.ddns.protocoin.eventbus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

class EventBusTest {
    private EventBus eventBus;

    @BeforeEach
    void setup() {
        eventBus = new EventBus();
    }

    @Test
    void shouldProperlySubscribeToEvent() {
        // given:
        var stringEventListenerMock = Mockito.mock(StringEventListener.class);
        var integerEventListenerMock = Mockito.mock(IntegerEventListener.class);
        when(stringEventListenerMock.getEventType()).thenReturn(StringEvent.class);
        when(integerEventListenerMock.getEventType()).thenReturn(IntegerEvent.class);
        var stringEvent = new StringEvent("event");
        var integerEvent = new IntegerEvent(4);

        // when:
        eventBus.registerListener(stringEventListenerMock);
        eventBus.registerListener(integerEventListenerMock);
        eventBus.postEvent(stringEvent);
        eventBus.postEvent(integerEvent);

        // then:
        verify(stringEventListenerMock, times(1)).handleEvent(stringEvent);
        verify(stringEventListenerMock, times(1)).handleEvent(Mockito.any());
        verify(integerEventListenerMock, times(1)).handleEvent(integerEvent);
        verify(integerEventListenerMock, times(1)).handleEvent(Mockito.any());
    }
}