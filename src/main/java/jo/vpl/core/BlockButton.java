package jo.vpl.core;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import jo.vpl.util.IconType;

/**
 *
 * @author JoostMeulenkamp
 */
public class BlockButton extends Button {

    IconType icon;
    IconType iconClicked;
    boolean clicked = false;

    public BlockButton(IconType type) {
        icon = type;
        getStyleClass().add("vpl-button");

        setText(type.getUnicode() + "");
    }

    public void setClickedType(IconType type) {
        setOnAction(this::handle_Clicked);
        iconClicked = type;
    }

    private void handle_Clicked(ActionEvent e) {
        if (!clicked) {
            setText(iconClicked.getUnicode() + "");
            clicked = true;
        } else {
            setText(icon.getUnicode() + "");
            clicked = false;
        }
    }
    
    public boolean isClicked(){
        return clicked;
    }
}
