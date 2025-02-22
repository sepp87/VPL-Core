package vplcore.graph.group;

import javafx.beans.property.*;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import vplcore.graph.base.BaseLabel;
import vplcore.workspace.WorkspaceController;

/**
 *
 * @author JoostMeulenkamp
 */
public class VplElement extends GridPane {

    public BaseLabel captionLabel;
    public HBox menuBox;

    public WorkspaceController workspaceController;

    public final StringProperty name = new SimpleStringProperty(this, "name", "");
    public final BooleanProperty selected = new SimpleBooleanProperty(this, "selected", false);
    public final BooleanProperty deleted = new SimpleBooleanProperty(this, "deleted", false);
    private final BooleanProperty active = new SimpleBooleanProperty(this, "active", false);

    private final EventHandler<MouseEvent> vplElementEnteredHandler = this::handleVplElementEntered;
    private final EventHandler<MouseEvent> vplElementExitedHandler = this::handleVplElementExited;

    public VplElement(WorkspaceController workspaceController) {
        this.workspaceController = workspaceController;

        getStyleClass().add("vpl-element");

        menuBox = new HBox(5);

        captionLabel = new BaseLabel(menuBox);
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
