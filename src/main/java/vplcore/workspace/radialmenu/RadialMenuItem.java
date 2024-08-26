package vplcore.workspace.radialmenu;

import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import vplcore.IconType;

/**
 *
 * @author joostmeulenkamp
 */
public class RadialMenuItem<T extends Enum<T>> extends Group {

    final T action;
    final IconType icon;
    final String name;

    double iconRotation = 0.;
    int index;
    double length;

    public RadialMenuItem(T action, IconType icon, String name) {
        this.action = action;
        this.icon = icon;
        this.name = name;
    }

    public RadialMenuItem(T action, IconType icon, String name, double iconRotation) {
        this.action = action;
        this.icon = icon;
        this.name = name;
        this.iconRotation = iconRotation;
    }

    public T getAction() {
        return action;
    }

    public Class<?> getActionType() {
        return action.getClass();
    }

}
