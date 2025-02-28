package vplcore.editor;

import vplcore.context.EditorMode;
import vplcore.workspace.WorkspaceModel;
import javafx.event.ActionEvent;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import vplcore.App;
import vplcore.Config;
import vplcore.util.NodeHierarchyUtils;
import vplcore.util.SystemUtils;
import vplcore.context.ActionManager;
import vplcore.context.EventRouter;
import vplcore.context.StateManager;
import vplcore.context.Command;
import vplcore.context.command.ApplyZoomCommand;
import vplcore.context.command.ZoomInCommand;
import vplcore.context.command.ZoomOutCommand;

/**
 * Manages zooming functionality and controls in the workspace.
 */
public class ZoomController extends BaseController {

    private final EventRouter eventRouter;
    private final ActionManager actionManager;
    private final StateManager state;
    private final WorkspaceModel model;
    private final ZoomView view;

    // To throttle zoom on macOS
    private long lastZoomTime = 0;
    private final long zoomThrottleInterval = 50;  // Throttle time in milliseconds (tune for macOS)

    public ZoomController(String contextId, WorkspaceModel workspaceModel, ZoomView zoomView) {
        super(contextId);
        this.eventRouter = App.getContext(contextId).getEventRouter();
        this.actionManager = App.getContext(contextId).getActionManager();
        this.state = App.getContext(contextId).getStateManager();
        this.model = workspaceModel;
        this.view = zoomView;

        view.getZoomInButton().setOnAction(this::handleZoomIn);
        view.getZoomOutButton().setOnAction(this::handleZoomOut);
        view.getZoomLabel().setOnMouseClicked(this::handleZoomReset);  // Reset zoom to 100% on click
        view.getZoomLabel().textProperty().bind(workspaceModel.zoomFactorProperty().multiply(100).asString("%.0f%%"));

        eventRouter.addEventListener(ScrollEvent.SCROLL_STARTED, this::handleScrollStarted);
        eventRouter.addEventListener(ScrollEvent.SCROLL, this::handleScrollUpdated);
        eventRouter.addEventListener(ScrollEvent.SCROLL_FINISHED, this::handleScrollFinished);
    }

    private void handleZoomIn(ActionEvent event) {
        Command command = new ZoomInCommand(actionManager.getWorkspaceController());
        actionManager.executeCommand(command);
    }

    private void handleZoomOut(ActionEvent event) {
        Command command = new ZoomOutCommand(actionManager.getWorkspaceController());
        actionManager.executeCommand(command);
    }

    private void handleZoomReset(MouseEvent event) {
        // Zoom is not from scrolling; no pivot point needed, since scene center is
        Command command = new ApplyZoomCommand(actionManager.getWorkspaceController(), 1.0, null);
        actionManager.executeCommand(command);
    }

    public void handleScrollStarted(ScrollEvent event) {
        // Scroll started is not triggered on Mac with a normal mouse
        if (state.isIdle()) {
            state.setZooming();
        }
    }

    // Create and return the ScrollEvent handler for SCROLL
    public void handleScrollUpdated(ScrollEvent event) {

        boolean onMac = Config.get().operatingSystem() == SystemUtils.OperatingSystem.MACOS;
        boolean isZoomModeAndOnMac = state.isZooming() && onMac;
        boolean isIdleAndNotOnMac = state.isIdle() && !onMac;

        Node intersectedNode = event.getPickResult().getIntersectedNode();
        boolean onScrollPane = NodeHierarchyUtils.isNodeOrParentOfType(intersectedNode, ScrollPane.class);
        boolean onListView = NodeHierarchyUtils.isNodeOrParentOfType(intersectedNode, ListView.class);

//        if (!onScrollPane && !onListView && (isZoomModeAndOnMac || isIdleAndNotOnMac)) {
        if (!onScrollPane && !onListView && (state.isIdle() || isIdleAndNotOnMac)) {

            // Throttle zoom on macOS
            if (onMac) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastZoomTime < zoomThrottleInterval) {
                    return;  // Ignore if throttling is active
                }
                lastZoomTime = currentTime;  // Update the last zoom time
            }

            double newScale;
            // Adjust zoom factor based on scroll direction
            if (event.getDeltaY() > 0) {
                newScale = model.getIncrementedZoomFactor();
            } else {
                newScale = model.getDecrementedZoomFactor();
            }
            Point2D pivotPoint = new Point2D(event.getSceneX(), event.getSceneY());

            // Zoom from scrolling; keep zoom centered around mouse position
            Command command = new ApplyZoomCommand(actionManager.getWorkspaceController(), newScale, pivotPoint);
            actionManager.executeCommand(command);
        }
    }

    public void handleScrollFinished(ScrollEvent event) {
        if (state.isZooming()) {
            state.setIdle();
        }
    }

}
