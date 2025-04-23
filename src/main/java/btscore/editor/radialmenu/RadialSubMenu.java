package btscore.editor.radialmenu;

import btscore.icons.FontAwesomeSolid;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author joostmeulenkamp
 */
public class RadialSubMenu extends RadialMenuItem {

    final List<RadialMenuItem> items = new ArrayList<>();

    public RadialSubMenu(String name, FontAwesomeSolid icon) {
        super(name, null, icon);
    }

    public RadialSubMenu(String name, FontAwesomeSolid icon, double iconRotation) {
        super(name, null, icon, iconRotation);
    }

}
