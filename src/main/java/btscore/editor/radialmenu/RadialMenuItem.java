package btscore.editor.radialmenu;

import btscore.icons.FontAwesomeIcon;
import javafx.scene.Group;

/**
 *
 * @author joostmeulenkamp
 */
public class RadialMenuItem extends Group {

    final String name;
    final FontAwesomeIcon icon;
    double iconRotation = 0.;

    int index;
    double length;

    public RadialMenuItem(String name, String id, FontAwesomeIcon icon) {
        this.name = name;
        this.idProperty().set(id);
        this.icon = icon;
    }

    public RadialMenuItem(String name, String id, FontAwesomeIcon icon, double iconRotation) {
        this.name = name;
        this.idProperty().set(id);
        this.icon = icon;
        this.iconRotation = iconRotation;
    }
}
