package btscore.context.event;

import javafx.event.Event;
import javafx.event.EventType;

/**
 *
 * @author Joost
 */
public class CustomMouseEvent extends Event {

    public static final EventType<CustomMouseEvent> DOUBLE_CLICKED_EVENT_TYPE = new EventType<>(Event.ANY, "DOUBLE_CLICKED_EVENT");
    public static final EventType<CustomMouseEvent> NOT_DOUBLE_CLICKED_EVENT_TYPE = new EventType<>(Event.ANY, "NOT_DOUBLE_CLICKED_EVENT");
    public static final EventType<CustomMouseEvent> LEFT_CLICKED_EVENT_TYPE = new EventType<>(Event.ANY, "LEFT_CLICKED_EVENT");
    public static final EventType<CustomMouseEvent> NOT_LEFT_CLICKED_EVENT_TYPE = new EventType<>(Event.ANY, "NOT_LEFT_CLICKED_EVENT");
    public static final EventType<CustomMouseEvent> RIGHT_CLICKED_EVENT_TYPE = new EventType<>(Event.ANY, "RIGHT_CLICKED_EVENT");
    public static final EventType<CustomMouseEvent> NOT_RIGHT_CLICKED_EVENT_TYPE = new EventType<>(Event.ANY, "NOT_RIGHT_CLICKED_EVENT");

    private final Object source;
    private final double sceneX;
    private final double sceneY;

    public CustomMouseEvent(EventType<CustomMouseEvent> eventType, Object source, double sceneX, double sceneY) {
        super(eventType);
        this.source = source;
        this.sceneX = sceneX;
        this.sceneY = sceneY;
    }

    public Object getSource() {
        return source;
    }

    public double getSceneX() {
        return sceneX;
    }

    public double getSceneY() {
        return sceneY;
    }
}
