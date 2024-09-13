package vplcore.graph.model;

import vplcore.graph.util.SelectBlock;
import javafx.beans.property.*;
import javafx.event.ActionEvent;
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

    public VplButton autoCheckBox;
    public VplButton binButton;
    public BlockLabel captionLabel;
    public VplButton questionButton;
    public VplButton resizeButton;
    public HBox menuBox;

    public Workspace workspace;
    public final StringProperty name = new SimpleStringProperty(this, "name", "");
    public final BooleanProperty selected = new SimpleBooleanProperty(this, "selected", false);
    public final BooleanProperty deleted = new SimpleBooleanProperty(this, "deleted", false);
    private final BooleanProperty active = new SimpleBooleanProperty(this, "active", false);
    public EventBlaster eventBlaster = new EventBlaster(this);

    private final EventHandler<MouseEvent> vplElementEnteredHandler = this::handleVplElementEntered;
    private final EventHandler<MouseEvent> vplElementExitedHandler = this::handleVplElementExited;
    private final EventHandler<ActionEvent> binButtonClickedHandler = this::handleBinButtonClicked;

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

        if (this instanceof SelectBlock) {
            return;
        }

        menuBox = new HBox(5);

        captionLabel = new BlockLabel(menuBox);
        captionLabel.getStyleClass().add("vpl-tag");
        captionLabel.textProperty().bindBidirectional(name);
        questionButton = new VplButton(IconType.FA_QUESTION_CIRCLE);
        resizeButton = new VplButton(IconType.FA_PLUS_SQUARE_O);
        binButton = new VplButton(IconType.FA_MINUS_CIRCLE);
        autoCheckBox = new VplButton(IconType.FA_CHECK_CIRCLE);
        autoCheckBox.setIconWhenClicked(IconType.FA_CIRCLE_O);

        captionLabel.setVisible(false);
        binButton.setVisible(false);
        questionButton.setVisible(false);
        resizeButton.setVisible(false);
        autoCheckBox.setVisible(false);

        menuBox.setAlignment(Pos.BOTTOM_LEFT);
        menuBox.getStyleClass().add("block-header");
        menuBox.getChildren().addAll(captionLabel, autoCheckBox, questionButton, binButton);

        // Add event handlers
        binButton.setOnAction(binButtonClickedHandler);
        addEventHandler(MouseEvent.MOUSE_ENTERED, vplElementEnteredHandler);
        addEventHandler(MouseEvent.MOUSE_EXITED, vplElementExitedHandler);

        add(menuBox, 1, 0);
    }

    public void handleBinButtonClicked(ActionEvent event) {
        delete();
    }

    public void delete() {
        removeEventHandler(MouseEvent.MOUSE_ENTERED, vplElementEnteredHandler);
        removeEventHandler(MouseEvent.MOUSE_EXITED, vplElementExitedHandler);

        
        
        binButton.setOnAction(null);
        autoCheckBox.setOnAction(null);
        captionLabel.textProperty().unbindBidirectional(name);
        captionLabel.delete();
        workspace.getChildren().remove(this);
        setDeleted(true);
    }

    public void handleVplElementEntered(MouseEvent event) {
        //Point to this by calling VPL Element
        if (VplElement.this instanceof SelectBlock) {
            return;
        }
        toggleControlsVisibility(true);
    }

    public void handleVplElementExited(MouseEvent element) {
        if (VplElement.this instanceof SelectBlock) {
            return;
        }
        toggleControlsVisibility(false);
    }

    private void toggleControlsVisibility(boolean isVisible) {
        captionLabel.setVisible(isVisible);
        binButton.setVisible(isVisible);
        questionButton.setVisible(isVisible);
        resizeButton.setVisible(isVisible);
        autoCheckBox.setVisible(isVisible);
    }

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
}
