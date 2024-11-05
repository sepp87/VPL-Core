package vplcore.editor;

import vplcore.workspace.WorkspaceModel;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import vplcore.App;
import vplcore.context.EventRouter;
import vplcore.context.StateManager;
import static vplcore.util.EditorUtils.onFreeSpace;

/**
 *
 * @author joostmeulenkamp
 */
public class PanController extends BaseController {

    private final EventRouter eventRouter;
    private final StateManager state;
    private final WorkspaceModel workspaceModel;

    private double initialX;
    private double initialY;
    private double initialTranslateX;
    private double initialTranslateY;

    public PanController(String contextId, WorkspaceModel workspaceModel) {
        super(contextId);
        this.eventRouter = App.getContext(contextId).getEventRouter();
        this.state = App.getContext(contextId).getStateManager();
        this.workspaceModel = workspaceModel;

        eventRouter.addEventListener(MouseEvent.MOUSE_PRESSED, this::handlePanStarted);
        eventRouter.addEventListener(MouseEvent.MOUSE_DRAGGED, this::handlePanUpdated);
        eventRouter.addEventListener(MouseEvent.MOUSE_RELEASED, this::handlePanFinished);
    }

    public void handlePanStarted(MouseEvent event) {
        boolean onFreeSpace = onFreeSpace(event);
        boolean isSecondary = event.getButton() == MouseButton.SECONDARY;
        if (onFreeSpace && state.isIdle() && isSecondary) {
            state.setPanning();
            initialX = event.getSceneX();
            initialY = event.getSceneY();
            initialTranslateX = workspaceModel.translateXProperty().get();
            initialTranslateY = workspaceModel.translateYProperty().get();
        }
    }

    public void handlePanUpdated(MouseEvent event) {
        boolean isSecondary = event.getButton() == MouseButton.SECONDARY;
        if (state.isPanning() && isSecondary) {
            workspaceModel.translateXProperty().set(initialTranslateX + event.getSceneX() - initialX);
            workspaceModel.translateYProperty().set(initialTranslateY + event.getSceneY() - initialY);
        }
    }

    public void handlePanFinished(MouseEvent event) {
        if (state.isPanning() && event.getButton() == MouseButton.SECONDARY) {
            state.setIdle();
        }
    }

}
