package vplcore.workspace.input;

import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import vplcore.graph.model.Block;
import vplcore.workspace.Actions;
import vplcore.workspace.Workspace;

/**
 *
 * @author joostmeulenkamp
 */
public class SelectionHandler {

    private final Workspace workspace;

    public SelectionHandler(Workspace workspace) {
        this.workspace = workspace;
        addInputHandlers();
    }

    private void addInputHandlers() {

        workspace.getScene().addEventFilter(MouseEvent.MOUSE_PRESSED, mousePressedHandler);
        workspace.getScene().addEventFilter(MouseEvent.MOUSE_DRAGGED, mouseDraggedHandler);
        workspace.getScene().addEventFilter(MouseEvent.MOUSE_RELEASED, mouseReleasedHandler);

    }

    private final EventHandler<MouseEvent> mousePressedHandler = new EventHandler<>() {
        @Override
        public void handle(MouseEvent event) {
            if (workspace.getMouseMode() == MouseMode.MOUSE_IDLE && event.isPrimaryButtonDown() && !workspace.onBlock(event) && !workspace.onBlockInfoPanel(event)) {
                workspace.setMouseMode(MouseMode.SELECTING);
                prepareSelectionRectangle(event);

            }
        }
    };

    private final EventHandler<MouseEvent> mouseDraggedHandler = new EventHandler<>() {
        @Override
        public void handle(MouseEvent event) {

            boolean isSelecting = workspace.startSelectionPoint != null;
//            if (event.isPrimaryButtonDown() && isSelecting) {
            if (workspace.getMouseMode() == MouseMode.SELECTING && event.isPrimaryButtonDown()) {
                startSelectionRectangleIfNull();
                updateSelectionRectangle(event);
                updateSelection();
            }
        }
    };

    private final EventHandler<MouseEvent> mouseReleasedHandler = new EventHandler<>() {
        @Override
        public void handle(MouseEvent event) {
            // Reset the start selection point
            workspace.startSelectionPoint = null;
            if (event.getButton() == MouseButton.PRIMARY) {

                if (workspace.getMouseMode() == MouseMode.SELECTING) {
                    // Reset the mouse mode back to idle
                    workspace.setMouseMode(MouseMode.MOUSE_IDLE);
                    // Check if selection rectangle is active
                    if (workspace.selectionRectangle != null) {
                        // Finalize selection by removing the selection rectangle
                        removeSelectionRectangle();
                    } else {
                        // Deselect all blocks if no selection rectangle was active
                        Actions.deselectAllBlocks(workspace);
                    }
                }
            }
        }
    };

    private void prepareSelectionRectangle(MouseEvent event) {
        workspace.startSelectionPoint = workspace.sceneToLocal(event.getSceneX(), event.getSceneY());
    }

    private void startSelectionRectangleIfNull() {
        if (workspace.selectionRectangle != null) {
            return;
        }
        workspace.selectionRectangle = new Region();
        workspace.selectionRectangle.setLayoutX(workspace.startSelectionPoint.getX());
        workspace.selectionRectangle.setLayoutY(workspace.startSelectionPoint.getY());
        workspace.selectionRectangle.setMinSize(0, 0);

        workspace.selectionRectangle.getStyleClass().add("selection-rectangle");
        workspace.getChildren().add(workspace.selectionRectangle);
    }

    private void updateSelectionRectangle(MouseEvent event) {

        Point2D currentPosition = workspace.sceneToLocal(event.getSceneX(), event.getSceneY());
        Point2D delta = currentPosition.subtract(workspace.startSelectionPoint);

        if (delta.getX() < 0) {
            workspace.selectionRectangle.setLayoutX(currentPosition.getX());
        }

        if (delta.getY() < 0) {
            workspace.selectionRectangle.setLayoutY(currentPosition.getY());
        }

        workspace.selectionRectangle.setMinSize(Math.abs(delta.getX()), Math.abs(delta.getY()));

    }

    private void updateSelection() {
        for (Block block : workspace.blockSet) {
            if ((block.getLayoutX() >= workspace.selectionRectangle.getLayoutX())
                    && block.getLayoutX() + block.getWidth() <= workspace.selectionRectangle.getLayoutX() + workspace.selectionRectangle.getWidth()
                    && (block.getLayoutY() >= workspace.selectionRectangle.getLayoutY()
                    && block.getLayoutY() + block.getHeight() <= workspace.selectionRectangle.getLayoutY() + workspace.selectionRectangle.getHeight())) {
                workspace.selectedBlockSet.add(block);
                block.setSelected(true);
            } else {
                workspace.selectedBlockSet.remove(block);
                block.setSelected(false);
            }
        }
    }

    private void removeSelectionRectangle() {
        workspace.getChildren().remove(workspace.selectionRectangle);
        workspace.selectionRectangle = null;
    }

}
