package vplcore.editor;

import javafx.geometry.Point2D;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import vplcore.graph.model.Block;
import vplcore.workspace.Actions;
import vplcore.workspace.Workspace;
import vplcore.workspace.input.MouseMode;

/**
 *
 * @author joostmeulenkamp
 */
public class SelectionRectangleView {

    private final Workspace workspace;
    private Point2D startSelectionPoint;
    private Region selectionRectangle;

    public SelectionRectangleView(Workspace workspace) {
        this.workspace = workspace;
    }


    public void handleMousePressed(MouseEvent event) {
        if (workspace.getMouseMode() == MouseMode.MOUSE_IDLE && event.isPrimaryButtonDown() && !workspace.onBlock(event) && !workspace.onBlockInfoPanel(event)) {
            workspace.setMouseMode(MouseMode.SELECTING);
            prepareSelectionRectangle(event);
        }
    }

    public void handleMouseDragged(MouseEvent event) {

        if (workspace.getMouseMode() == MouseMode.SELECTING && event.isPrimaryButtonDown()) {
            startSelectionRectangleIfNull();
            updateSelectionRectangle(event);
            updateSelection();
        }
    }

    public void handleMouseReleased(MouseEvent event) {
        // Reset the start selection point
        startSelectionPoint = null;
        if (event.getButton() == MouseButton.PRIMARY) {

            if (workspace.getMouseMode() == MouseMode.SELECTING) {
                // Reset the mouse mode back to idle
                workspace.setMouseMode(MouseMode.MOUSE_IDLE);
                // Check if selection rectangle is active
                if (selectionRectangle != null) {
                    // Finalize selection by removing the selection rectangle
                    removeSelectionRectangle();
                } else {
                    // Deselect all blocks if no selection rectangle was active
                    Actions.deselectAllBlocks(workspace);
                }
            }
        }
    }

    private void prepareSelectionRectangle(MouseEvent event) {
        startSelectionPoint = workspace.sceneToLocal(event.getSceneX(), event.getSceneY());
    }

    private void startSelectionRectangleIfNull() {
        if (selectionRectangle != null) {
            return;
        }
        selectionRectangle = new Region();
        selectionRectangle.setLayoutX(startSelectionPoint.getX());
        selectionRectangle.setLayoutY(startSelectionPoint.getY());
        selectionRectangle.setMinSize(0, 0);

        selectionRectangle.getStyleClass().add("selection-rectangle");
        workspace.getChildren().add(selectionRectangle);
    }

    private void updateSelectionRectangle(MouseEvent event) {

        Point2D currentPosition = workspace.sceneToLocal(event.getSceneX(), event.getSceneY());
        Point2D delta = currentPosition.subtract(startSelectionPoint);

        if (delta.getX() < 0) {
            selectionRectangle.setLayoutX(currentPosition.getX());
        }

        if (delta.getY() < 0) {
            selectionRectangle.setLayoutY(currentPosition.getY());
        }

        selectionRectangle.setMinSize(Math.abs(delta.getX()), Math.abs(delta.getY()));

    }

    private void updateSelection() {
        for (Block block : workspace.blockSet) {
            if ((block.getLayoutX() >= selectionRectangle.getLayoutX())
                    && block.getLayoutX() + block.getWidth() <= selectionRectangle.getLayoutX() + selectionRectangle.getWidth()
                    && (block.getLayoutY() >= selectionRectangle.getLayoutY()
                    && block.getLayoutY() + block.getHeight() <= selectionRectangle.getLayoutY() + selectionRectangle.getHeight())) {
                workspace.selectedBlockSet.add(block);
                block.setSelected(true);
            } else {
                workspace.selectedBlockSet.remove(block);
                block.setSelected(false);
            }
        }
    }

    private void removeSelectionRectangle() {
        workspace.getChildren().remove(selectionRectangle);
        selectionRectangle = null;
    }

}
