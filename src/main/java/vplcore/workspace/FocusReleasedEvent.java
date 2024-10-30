package vplcore.workspace;

import javafx.event.Event;
import javafx.event.EventType;

/**
 *
 * @author Joost
 */
public class FocusReleasedEvent extends Event {

    public static final EventType<FocusReleasedEvent> FOCUS_RELEASED_EVENT_TYPE = new EventType<>(Event.ANY, "FOCUS_RELEASED_EVENT");

    public FocusReleasedEvent() {
        super(FOCUS_RELEASED_EVENT_TYPE);
    }

}
