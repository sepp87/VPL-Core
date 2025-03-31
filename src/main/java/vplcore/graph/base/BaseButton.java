package vplcore.graph.base;

import javafx.scene.control.Button;
import javafx.scene.layout.Region;
import vplcore.IconType;

/**
 *
 * @author JoostMeulenkamp
 */
public class BaseButton extends Button {

    public BaseButton(IconType type) {
        getStyleClass().add("vpl-button");
        setText(type.getUnicode() + "");
        setFocusTraversable(false);
        setMinWidth(Region.USE_PREF_SIZE);
        setPrefWidth(Region.USE_COMPUTED_SIZE);
    }

}
