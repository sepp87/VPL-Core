package btscore.editor.radialmenu;

import javafx.scene.Group;
import btscore.IconType;

/**
 *
 * @author joostmeulenkamp
 */
public class RadialMenuItem extends Group {

    final String name;
    final IconType icon;
    double iconRotation = 0.;
    
    int index;
    double length;

    public RadialMenuItem(String name, String id, IconType icon) {
        this.name = name;
        this.idProperty().set(id);
        this.icon = icon;
    }

    public RadialMenuItem(String name, String id, IconType icon, double iconRotation) {
        this.name = name;
        this.idProperty().set(id);
        this.icon = icon;
        this.iconRotation = iconRotation;
    }

}
