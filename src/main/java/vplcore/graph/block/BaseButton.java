package vplcore.graph.block;

import javafx.scene.control.Button;
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
    }

}
