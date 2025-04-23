package btscore.graph.base;

import btscore.icons.FontAwesomeIcon;
import btscore.icons.FontAwesomeSolid;
import javafx.scene.control.Button;
import javafx.scene.layout.Region;

/**
 *
 * @author JoostMeulenkamp
 */
public class BaseButton extends Button {

    private FontAwesomeIcon icon;

    public BaseButton(FontAwesomeIcon icon) {
        this.icon = icon;
        setText(icon.unicode());
        setFocusTraversable(false);
        setMinWidth(Region.USE_PREF_SIZE);
        setPrefWidth(Region.USE_COMPUTED_SIZE);
        setStyle();
    }

    public void setText(FontAwesomeIcon icon) {
        setText(icon.unicode());
        setStyle();
    }

    private void setStyle() {
        getStyleClass().clear();
        getStyleClass().add("vpl-button");
        String fontStyle = icon instanceof FontAwesomeSolid ? "font-awesome-solid" : "font-awesome-regular";
        getStyleClass().add(fontStyle);
    }

}
