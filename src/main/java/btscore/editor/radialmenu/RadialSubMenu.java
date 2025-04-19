package btscore.editor.radialmenu;

import java.util.ArrayList;
import java.util.List;
import btscore.IconType;

/**
 *
 * @author joostmeulenkamp
 */
public class RadialSubMenu extends RadialMenuItem {

    final List<RadialMenuItem> items = new ArrayList<>();

    public RadialSubMenu(String name, IconType icon) {
        super(name, null, icon);
    }

    public RadialSubMenu(String name, IconType icon, double iconRotation) {
        super(name, null, icon, iconRotation);
    }

}
