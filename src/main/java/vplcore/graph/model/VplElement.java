package vplcore.graph.model;

import javafx.beans.property.*;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import vplcore.EventBlaster;
import vplcore.workspace.WorkspaceController;
import vplcore.workspace.WorkspaceView;

/**
 *
 * @author JoostMeulenkamp
 */
public class VplElement extends GridPane {

    public BlockLabel captionLabel;
    public HBox menuBox;

    public WorkspaceController workspaceController;

    public final StringProperty name = new SimpleStringProperty(this, "name", "");
    public final BooleanProperty selected = new SimpleBooleanProperty(this, "selected", false);
    public final BooleanProperty deleted = new SimpleBooleanProperty(this, "deleted", false);
    private final BooleanProperty active = new SimpleBooleanProperty(this, "active", false);
    public EventBlaster eventBlaster = new EventBlaster(this);

    private final EventHandler<MouseEvent> vplElementEnteredHandler = this::handleVplElementEntered;
    private final EventHandler<MouseEvent> vplElementExitedHandler = this::handleVplElementExited;

    public VplElement(WorkspaceController workspaceController) {
        this.workspaceController = workspaceController;

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

        menuBox = new HBox(5);

        captionLabel = new BlockLabel(menuBox);
        captionLabel.getStyleClass().add("vpl-tag");
        captionLabel.textProperty().bindBidirectional(name);
        captionLabel.setVisible(false);

        menuBox.setAlignment(Pos.BOTTOM_LEFT);
        menuBox.getStyleClass().add("block-header");
        menuBox.getChildren().addAll(captionLabel);

        // Add event handlers
        addEventHandler(MouseEvent.MOUSE_ENTERED, vplElementEnteredHandler);
        addEventHandler(MouseEvent.MOUSE_EXITED, vplElementExitedHandler);

        add(menuBox, 1, 0);
    }

    public void delete() {
        removeEventHandler(MouseEvent.MOUSE_ENTERED, vplElementEnteredHandler);
        removeEventHandler(MouseEvent.MOUSE_EXITED, vplElementExitedHandler);

        captionLabel.textProperty().unbindBidirectional(name);
        captionLabel.delete();
//        workspaceController.removeChild( this);
//        workspaceView.getChildren().remove(this);
        setDeleted(true);
    }

    public void handleVplElementEntered(MouseEvent event) {
        captionLabel.setVisible(true);
    }

    public void handleVplElementExited(MouseEvent event) {
        captionLabel.setVisible(false);
    }

    private void toggleControlsVisibility(boolean isVisible) {
        captionLabel.setVisible(isVisible);
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
