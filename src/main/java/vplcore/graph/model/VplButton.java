package vplcore.graph.model;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import vplcore.IconType;

/**
 *
 * @author JoostMeulenkamp
 */
public class VplButton extends Button {

    IconType icon;
    IconType iconWhenClicked;
    boolean clicked = false;

    private final EventHandler<ActionEvent> buttonClickedHandler = this::handleButtonClicked;

    public VplButton(IconType type) {
        icon = type;
        getStyleClass().add("vpl-button");
        setText(type.getUnicode() + "");
    }

    public void setIconWhenClicked(IconType type) {
        setOnAction(buttonClickedHandler); // TODO pointless event handler, since it gets removed as soon as an action is set to it
        iconWhenClicked = type;
    }

    private void handleButtonClicked(ActionEvent event) {
        if (!clicked) {
            setText(iconWhenClicked.getUnicode() + "");
            clicked = true;
        } else {
            setText(icon.getUnicode() + "");
            clicked = false;
        }
    }

    public boolean isClicked() {
        return clicked;
    }
}
