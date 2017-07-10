package jo.vpl.core;

import javafx.beans.property.*;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import jo.vpl.util.IconType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

/**
 *
 * @author JoostMeulenkamp
 */
public class VplElement extends GridPane {

    public HubButton autoCheckBox;
    public HubButton binButton;
    public HubLabel captionLabel;
    public HubButton questionButton;
    public HubButton resizeButton;
    public HBox menuBox;

    public VplControl hostCanvas;
    public final StringProperty name = new SimpleStringProperty(this, "name", "");
    public final BooleanProperty selected = new SimpleBooleanProperty(this, "selected", false);
    public final BooleanProperty deleted = new SimpleBooleanProperty(this, "deleted", false);
    private final BooleanProperty active = new SimpleBooleanProperty(this, "active", false);
    public EventBlaster eventBlaster = new EventBlaster(this);

    public VplElement(VplControl vplControl) {

        hostCanvas = vplControl;

        getStyleClass().add("vpl-element");

        //@TODO might want to split up into geometry and semantics
        eventBlaster.set("left", layoutXProperty());
        eventBlaster.set("top", layoutYProperty());
        eventBlaster.set("translateLeft", translateXProperty());
        eventBlaster.set("translateTop", translateYProperty());
        eventBlaster.set("size", boundsInParentProperty());
        eventBlaster.set("name", name);
        eventBlaster.set("selected", selected);
        eventBlaster.set("deleted", deleted);

        //Event handlers replace anonymous handlers otherwise they conflict with the
        //ones created within a Hub, which leads to missing VPL Element buttons
        this.addEventFilter(MouseEvent.MOUSE_ENTERED, onMouseEnterEventHandler);
        this.addEventFilter(MouseEvent.MOUSE_EXITED, onMouseExitEventHandler);
//        setOnMouseEntered(this::handle_MouseEnter);
//        setOnMouseExited(this::handle_MouseExit);

        if (this instanceof SelectHub) {
            return;
        }

        menuBox = new HBox(5);

        captionLabel = new HubLabel(menuBox);
        captionLabel.getStyleClass().add("vpl-tag");
        captionLabel.textProperty().bindBidirectional(name);
        questionButton = new HubButton(IconType.FA_QUESTION_CIRCLE);
        resizeButton = new HubButton(IconType.FA_PLUS_SQUARE_O);
        binButton = new HubButton(IconType.FA_MINUS_CIRCLE);
        binButton.setOnMouseClicked(this::binButton_MouseClick);
        autoCheckBox = new HubButton(IconType.FA_CHECK_CIRCLE);
        autoCheckBox.setClickedType(IconType.FA_CIRCLE_O);

        captionLabel.setVisible(false);
        binButton.setVisible(false);
        questionButton.setVisible(false);
        resizeButton.setVisible(false);
        autoCheckBox.setVisible(false);

        menuBox.setAlignment(Pos.BOTTOM_LEFT);
        menuBox.getStyleClass().add("hub-header");
        menuBox.getChildren().addAll(captionLabel, autoCheckBox, questionButton, binButton);

        add(menuBox, 1, 0);
    }

    public void binButton_MouseClick(MouseEvent e) {
        removeEventFilter(MouseEvent.MOUSE_ENTERED, onMouseEnterEventHandler);
        removeEventFilter(MouseEvent.MOUSE_EXITED, onMouseExitEventHandler);
        delete();
    }

    public void delete() {
        hostCanvas.getChildren().remove(this);
        setDeleted(true);
    }

    private final EventHandler<MouseEvent> onMouseEnterEventHandler = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent e) {
            //Point to this by calling VPL Element
            if (VplElement.this instanceof SelectHub) {
                return;
            }

            captionLabel.setVisible(true);
            binButton.setVisible(true);
            questionButton.setVisible(true);
            resizeButton.setVisible(true);
            autoCheckBox.setVisible(true);
        }
    };
    private final EventHandler<MouseEvent> onMouseExitEventHandler = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent e) {
            if (VplElement.this instanceof SelectHub) {
                return;
            }

            captionLabel.setVisible(false);
            binButton.setVisible(false);
            questionButton.setVisible(false);
            resizeButton.setVisible(false);
            autoCheckBox.setVisible(false);
        }
    };

    public Point2D getLocation() {
        return new Point2D(getLayoutX(), getLayoutY());
    }

    public final StringProperty nameProperty() {
        return name;
    }

    public final String getName() {
        return name.get();
    }

    public void setName(String value) {
        name.set(value);
    }

    public final boolean isSelected() {
        return selected.get();
    }

    public void setSelected(boolean value) {
        selected.set(value);
    }

    public final BooleanProperty activeProperty() {
        return active;
    }

    public final boolean isActive() {
        return active.get();
    }

    public void setActive(boolean value) {
        active.set(value);
    }

    public final BooleanProperty deletedProperty() {
        return deleted;
    }

    public final boolean getDeleted() {
        return deleted.get();
    }

    public void setDeleted(boolean value) {
        deleted.set(value);
    }

//    private void handle_Select(PropertyChangeEvent e) {
//        if (isSelected()) {
//            getStyleClass().remove("vpl-element");
//            getStyleClass().add("vpl-element-active");
//        } else {
//            getStyleClass().remove("vpl-element-active");
//            getStyleClass().add("vpl-element");
//        }
//    }
}
