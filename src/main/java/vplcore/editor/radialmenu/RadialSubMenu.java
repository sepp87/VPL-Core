package vplcore.editor.radialmenu;

import java.util.ArrayList;
import java.util.List;
import vplcore.IconType;

/**
 *
 * @author joostmeulenkamp
 */
public class RadialSubMenu<T extends Enum<T>> extends RadialMenuItem<T> {

    final List<RadialMenuItem<?>> items = new ArrayList<>();

    public RadialSubMenu(IconType icon, String name) {
        super(null, icon, name);
    }

    public RadialSubMenu(IconType icon, String name, double iconRotation) {
        super(null, icon, name, iconRotation);
    }

}
