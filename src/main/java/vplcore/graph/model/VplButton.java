package vplcore.graph.model;

import javafx.scene.control.Button;
import vplcore.IconType;

/**
 *
 * @author JoostMeulenkamp
 */
public class VplButton extends Button {

    public VplButton(IconType type) {
        getStyleClass().add("vpl-button");
        setText(type.getUnicode() + "");
    }

}
