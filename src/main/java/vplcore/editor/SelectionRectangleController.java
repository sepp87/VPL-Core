package vplcore.editor;

import javafx.geometry.Point2D;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import vplcore.graph.model.Block;
import vplcore.workspace.Actions;
import vplcore.workspace.Workspace;
import vplcore.workspace.input.MouseMode;

/**
 *
 * @author joostmeulenkamp
 */
public class SelectionRectangleController {

    private final SelectionRectangleView view;
    private final Workspace workspace;

    private Point2D startPoint;

    public SelectionRectangleController(SelectionRectangleView selectionRectangleView, Workspace workspace) {
        this.view = selectionRectangleView;
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
            initializeSelectionRectangle();
            updateSelectionRectangle(event);
            updateSelection();
        }
    }

    public void handleMouseReleased(MouseEvent event) {
        // Reset the start selection point
        startPoint = null;
        if (event.getButton() == MouseButton.PRIMARY) {

            if (workspace.getMouseMode() == MouseMode.SELECTING) {
                // Reset the mouse mode back to idle
                workspace.setMouseMode(MouseMode.MOUSE_IDLE);
                // Check if selection rectangle is active
                if (view.isVisible()) {
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
        startPoint = new Point2D(event.getSceneX(), event.getSceneY());
    }

    private void initializeSelectionRectangle() {
        if (view.isVisible()) {
            return;
        }
        view.setVisible(true);
        view.setLayoutX(startPoint.getX());
        view.setLayoutY(startPoint.getY());
        view.setMinSize(0, 0);
    }

    private void updateSelectionRectangle(MouseEvent event) {

        Point2D currentPosition = new Point2D(event.getSceneX(), event.getSceneY());
        Point2D delta = currentPosition.subtract(startPoint);

        if (delta.getX() < 0) {
            view.setLayoutX(currentPosition.getX());
        }

        if (delta.getY() < 0) {
            view.setLayoutY(currentPosition.getY());
        }

        view.setMinSize(Math.abs(delta.getX()), Math.abs(delta.getY()));

    }

    private void updateSelection() {

        Point2D selectionMin = workspace.sceneToLocal(view.getLayoutX(), view.getLayoutY());
        Point2D selectionMax = workspace.sceneToLocal(view.getLayoutX() + view.getWidth(), view.getLayoutY() + view.getHeight());

        for (Block block : workspace.blockSet) {
            if (true // unnecessary statement for readability
                    && block.getLayoutX() >= selectionMin.getX()
                    && block.getLayoutX() + block.getWidth() <= selectionMax.getX()
                    && block.getLayoutY() >= selectionMin.getY()
                    && block.getLayoutY() + block.getHeight() <= selectionMax.getY()) {

                workspace.selectedBlockSet.add(block);
                block.setSelected(true);

            } else {
                workspace.selectedBlockSet.remove(block);
                block.setSelected(false);
            }
        }
    }

    private void removeSelectionRectangle() {
        view.setVisible(false);
    }

}
