package vplcore.graph.model;

import vplcore.graph.util.SelectBlock;
import javafx.beans.property.*;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import vplcore.IconType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import vplcore.EventBlaster;
import vplcore.workspace.Workspace;

/**
 *
 * @author JoostMeulenkamp
 */
public class VplElement extends GridPane {

    public BlockButton autoCheckBox;
    public BlockButton binButton;
    public BlockLabel captionLabel;
    public BlockButton questionButton;
    public BlockButton resizeButton;
    public HBox menuBox;

    public Workspace workspace;
    public final StringProperty name = new SimpleStringProperty(this, "name", "");
    public final BooleanProperty selected = new SimpleBooleanProperty(this, "selected", false);
    public final BooleanProperty deleted = new SimpleBooleanProperty(this, "deleted", false);
    private final BooleanProperty active = new SimpleBooleanProperty(this, "active", false);
    public EventBlaster eventBlaster = new EventBlaster(this);

    public VplElement(Workspace workspace) {

        this.workspace = workspace;

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
        //ones created within a block, which leads to missing VPL Element buttons
        this.addEventHandler(MouseEvent.MOUSE_ENTERED, onMouseEnterEventHandler);
        this.addEventHandler(MouseEvent.MOUSE_EXITED, onMouseExitEventHandler);

        if (this instanceof SelectBlock) {
            return;
        }

        menuBox = new HBox(5);

        captionLabel = new BlockLabel(menuBox);
        captionLabel.getStyleClass().add("vpl-tag");
        captionLabel.textProperty().bindBidirectional(name);
        questionButton = new BlockButton(IconType.FA_QUESTION_CIRCLE);
        resizeButton = new BlockButton(IconType.FA_PLUS_SQUARE_O);
        binButton = new BlockButton(IconType.FA_MINUS_CIRCLE);
        binButton.addEventHandler(MouseEvent.MOUSE_CLICKED, binButtonClickedHandler);
        autoCheckBox = new BlockButton(IconType.FA_CHECK_CIRCLE);
        autoCheckBox.setClickedType(IconType.FA_CIRCLE_O);

        captionLabel.setVisible(false);
        binButton.setVisible(false);
        questionButton.setVisible(false);
        resizeButton.setVisible(false);
        autoCheckBox.setVisible(false);

        menuBox.setAlignment(Pos.BOTTOM_LEFT);
        menuBox.getStyleClass().add("block-header");
        menuBox.getChildren().addAll(captionLabel, autoCheckBox, questionButton, binButton);

        add(menuBox, 1, 0);
    }

    private final EventHandler<MouseEvent> binButtonClickedHandler = new EventHandler<>() {
        @Override
        public void handle(MouseEvent event) {
            delete();
        }
    };

    public void delete() {
        removeEventHandler(MouseEvent.MOUSE_ENTERED, onMouseEnterEventHandler);
        removeEventHandler(MouseEvent.MOUSE_EXITED, onMouseExitEventHandler);
        removeEventHandler(MouseEvent.MOUSE_CLICKED, binButtonClickedHandler);
        workspace.getChildren().remove(this);
        setDeleted(true);
    }

    private final EventHandler<MouseEvent> onMouseEnterEventHandler = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent e) {
            //Point to this by calling VPL Element
            if (VplElement.this instanceof SelectBlock) {
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
            if (VplElement.this instanceof SelectBlock) {
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
