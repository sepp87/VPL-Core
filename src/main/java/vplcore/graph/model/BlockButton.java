package vplcore.graph.model;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import vplcore.IconType;

/**
 *
 * @author JoostMeulenkamp
 */
public class BlockButton extends Button {

    IconType icon;
    IconType iconClicked;
    boolean clicked = false;
    private final EventHandler<ActionEvent> buttonClickedHandler = createButtonClickedHandler();

    public BlockButton(IconType type) {
        icon = type;
        getStyleClass().add("vpl-button");

        setText(type.getUnicode() + "");
    }

    public void setClickedType(IconType type) {
        setOnAction(buttonClickedHandler);
        iconClicked = type;
    }

    private EventHandler<ActionEvent> createButtonClickedHandler() {
        return (ActionEvent event) -> {
            if (!clicked) {
                setText(iconClicked.getUnicode() + "");
                clicked = true;
            } else {
                setText(icon.getUnicode() + "");
                clicked = false;
            }
        };
    }

    public boolean isClicked() {
        return clicked;
    }
}
