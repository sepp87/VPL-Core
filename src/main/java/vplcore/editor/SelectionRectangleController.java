package vplcore.editor;

import javafx.geometry.Point2D;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import vplcore.App;
import vplcore.context.ActionManager;
import vplcore.context.EventRouter;
import vplcore.context.StateManager;
import vplcore.workspace.Command;
import vplcore.context.command.DeselectAllBlocksCommand;
import vplcore.context.command.RectangleSelectCommand;

/**
 *
 * @author joostmeulenkamp
 */
public class SelectionRectangleController extends BaseController {

    private final EventRouter eventRouter;
    private final ActionManager actionManager;
    private final StateManager state;
    private final SelectionRectangleView view;

    private Point2D startPoint;

    public SelectionRectangleController(String contextId, SelectionRectangleView selectionRectangleView) {
        super(contextId);
        this.eventRouter = App.getContext(contextId).getEventRouter();
        this.actionManager = App.getContext(contextId).getActionManager();
        this.state = App.getContext(contextId).getStateManager();
        this.view = selectionRectangleView;

        eventRouter.addEventListener(MouseEvent.MOUSE_PRESSED, this::handleSelectionStarted);
        eventRouter.addEventListener(MouseEvent.MOUSE_DRAGGED, this::handleSelectionUpdated);
        eventRouter.addEventListener(MouseEvent.MOUSE_RELEASED, this::handleSelectionFinished);
    }

    public void handleSelectionStarted(MouseEvent event) {
        boolean isPrimary = event.getButton() == MouseButton.PRIMARY;
        boolean isIdle = state.isIdle();

        if (isPrimary && isIdle) {
            state.setSelecting();
            prepareSelectionRectangle(event);
        }
    }

    public void handleSelectionUpdated(MouseEvent event) {
        boolean isPrimary = event.getButton() == MouseButton.PRIMARY;
        boolean isSelecting = state.isSelecting();

        if (isSelecting && isPrimary) {
            initializeSelectionRectangle();
            updateSelectionRectangle(event);
            updateSelection();
        }
    }

    public void handleSelectionFinished(MouseEvent event) {
        // do NOT reset startPoint to null, because this will throw null pointer exceptions, when accidentally clicking another button when selecting
        if (event.getButton() == MouseButton.PRIMARY) {
            if (state.isSelecting()) {
                // Reset the mouse mode back to idle
                state.setIdle();
                // Check if selection rectangle is active
                if (view.isVisible()) {
                    // Finalize selection by removing the selection rectangle
                    removeSelectionRectangle();
                } else {
                    // Deselect all blocks if no selection rectangle was active
                    Command command = new DeselectAllBlocksCommand(actionManager.getWorkspaceController());
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
        Command command = new RectangleSelectCommand(actionManager.getWorkspaceController(), selectionMin, selectionMax);
        actionManager.executeCommand(command);
    }

    private void removeSelectionRectangle() {
        view.setVisible(false);
    }

}
