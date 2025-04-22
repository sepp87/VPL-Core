package btscore.editor.context;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Joost
 */
public class EventRouter {

    private final Map<EventType<? extends Event>, Set<EventHandler<? super Event>>> listeners = new HashMap<>();

    // Register a listener for a specific event type
    public <T extends Event> void addEventListener(EventType<? extends T> type, EventHandler<? super T> handler) {
        listeners.computeIfAbsent(type, k -> new HashSet<>()).add((EventHandler<? super Event>) handler);
    }

    // Remove a listener for a specific event type
    public <T extends Event> void removeEventListener(EventType<? extends T> type, EventHandler<? super T> handler) {
        Set<EventHandler<? super Event>> handlers = listeners.get(type);
        if (handlers != null) {
            handlers.remove((EventHandler<? super Event>) handler);
        }
    }

    // Fire an event to all listeners of its type
    public void fireEvent(Event event) {
        Set<EventHandler<? super Event>> handlers = listeners.get(event.getEventType());
        if (handlers != null) {
            for (EventHandler<? super Event> handler : handlers) {
                handler.handle(event);
            }
        }
    }
}
