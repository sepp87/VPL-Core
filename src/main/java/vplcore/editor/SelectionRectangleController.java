package vplcore.editor;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import vplcore.workspace.ActionManager;
import vplcore.workspace.Command;
import vplcore.workspace.Workspace;
import vplcore.workspace.command.DeselectAllBlocksCommand;
import vplcore.workspace.command.RectangleSelectCommand;

/**
 *
 * @author joostmeulenkamp
 */
public class SelectionRectangleController {

    private final ActionManager actionManager;
    private final EditorModel editorModel;
    private final SelectionRectangleView view;

    private Point2D startPoint;

    public SelectionRectangleController(ActionManager actionManager, EditorModel editorModel, SelectionRectangleView selectionRectangleView) {
        this.actionManager = actionManager;
        this.editorModel = editorModel;
        this.view = selectionRectangleView;
    }

    public void processEditorSelectionStarted(MouseEvent event) {
        Node intersectedNode = event.getPickResult().getIntersectedNode();
        boolean onEditorOrWorkspace = intersectedNode instanceof EditorView || intersectedNode instanceof Workspace;
        boolean isPrimaryClick = event.getButton() == MouseButton.PRIMARY;
        boolean isIdle = editorModel.modeProperty().get() == EditorMode.IDLE_MODE;

        if (isPrimaryClick && onEditorOrWorkspace && isIdle) {
            editorModel.modeProperty().set(EditorMode.SELECTION_MODE);
            prepareSelectionRectangle(event);
        }
    }

    public void processEditorSelection(MouseEvent event) {
        if (editorModel.modeProperty().get() == EditorMode.SELECTION_MODE && event.isPrimaryButtonDown()) {
            initializeSelectionRectangle();
            updateSelectionRectangle(event);
            updateSelection();
        }
    }

    public void processEditorSelectionFinished(MouseEvent event) {
        // Reset the start selection point
        startPoint = null;
        if (event.getButton() == MouseButton.PRIMARY) {

            if (editorModel.modeProperty().get() == EditorMode.SELECTION_MODE) {
                // Reset the mouse mode back to idle
                editorModel.modeProperty().set(EditorMode.IDLE_MODE);
                // Check if selection rectangle is active
                if (view.isVisible()) {
                    // Finalize selection by removing the selection rectangle
                    removeSelectionRectangle();
                } else {
                    // Deselect all blocks if no selection rectangle was active
                    Command command = new DeselectAllBlocksCommand(actionManager.getWorkspace());
                    actionManager.executeCommand(command);
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
        Point2D selectionMin = new Point2D(view.getLayoutX(), view.getLayoutY());
        Point2D selectionMax = new Point2D(view.getLayoutX() + view.getWidth(), view.getLayoutY() + view.getHeight());
        Command command = new RectangleSelectCommand(actionManager.getWorkspace(), selectionMin, selectionMax);
        actionManager.executeCommand(command);
    }

    private void removeSelectionRectangle() {
        view.setVisible(false);
    }

}
