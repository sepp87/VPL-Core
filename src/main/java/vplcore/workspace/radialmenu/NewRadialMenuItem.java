package vplcore.workspace.radialmenu;

import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import vplcore.IconType;

/**
 *
 * @author joostmeulenkamp
 */
public class NewRadialMenuItem extends Group {

    IconType icon;
    double iconRotation = 0.;
    String name;
    int index;
    double length;

    public NewRadialMenuItem(IconType icon, String name) {
        this.icon = icon;
        this.name = name;
    }

    public NewRadialMenuItem(IconType icon, String name, double iconRotation) {
        this(icon, name);
        this.iconRotation = iconRotation;
    }

    public NewRadialMenuItem(IconType icon, String name, EventHandler<MouseEvent> onMouseClicked) {
        this.icon = icon;
        this.name = name;
        this.setOnMouseClicked(onMouseClicked);
    }

    public NewRadialMenuItem(IconType icon, String name, double iconRotation, EventHandler<MouseEvent> onMouseClicked) {
        this(icon, name);
        this.iconRotation = iconRotation;
        this.setOnMouseClicked(onMouseClicked);
    }

}
