package btscore.context.event;

import javafx.event.Event;
import javafx.event.EventType;

/**
 *
 * @author Joost
 */
public class FocusNotRequiredEvent extends Event {

    public static final EventType<FocusNotRequiredEvent> FOCUS_NOT_REQUIRED_EVENT = new EventType<>(Event.ANY, "FOCUS_NOT_REQUIRED_EVENT");

    public FocusNotRequiredEvent() {
        super(FOCUS_NOT_REQUIRED_EVENT);
    }

}
