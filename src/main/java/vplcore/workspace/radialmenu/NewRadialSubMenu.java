package vplcore.workspace.radialmenu;

import java.util.ArrayList;
import java.util.List;
import vplcore.IconType;

/**
 *
 * @author joostmeulenkamp
 */
public class NewRadialSubMenu extends NewRadialMenuItem {

    final List<NewRadialMenuItem> items = new ArrayList<>();

    public NewRadialSubMenu(IconType icon, String name) {
        super(icon, name);
    }

    public NewRadialSubMenu(IconType icon, String name, double iconRotation) {
        super(icon, name, iconRotation);
    }

}
