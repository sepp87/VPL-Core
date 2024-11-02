package vplcore.editor;

import vplcore.workspace.WorkspaceModel;
import javafx.event.ActionEvent;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import vplcore.Config;
import vplcore.util.NodeHierarchyUtils;
import vplcore.util.SystemUtils;
import vplcore.workspace.ActionManager;
import vplcore.workspace.Command;
import vplcore.workspace.command.ApplyZoomCommand;
import vplcore.workspace.command.ZoomInCommand;
import vplcore.workspace.command.ZoomOutCommand;

/**
 * Manages zooming functionality and controls in the workspace.
 */
public class ZoomController extends HBox {

    private final ActionManager actionManager;
    private final EditorModel editorModel;
    private final WorkspaceModel model;
    private final ZoomView view;

    // To throttle zoom on macOS
    private long lastZoomTime = 0;
    private final long zoomThrottleInterval = 50;  // Throttle time in milliseconds (tune for macOS)

    public ZoomController(ActionManager actionManager, EditorModel editorModel, WorkspaceModel zoomModel, ZoomView zoomView) {
        this.actionManager = actionManager;
        this.editorModel = editorModel;
        this.model = zoomModel;
        this.view = zoomView;

        view.getZoomInButton().setOnAction(this::handleZoomIn);
        view.getZoomOutButton().setOnAction(this::handleZoomOut);
        view.getZoomLabel().setOnMouseClicked(this::handleZoomReset);  // Reset zoom to 100% on click
        view.getZoomLabel().textProperty().bind(zoomModel.zoomFactorProperty().multiply(100).asString("%.0f%%"));
    }

    private void handleZoomIn(ActionEvent event) {
        Command command = new ZoomInCommand(actionManager.getWorkspace());
        actionManager.executeCommand(command);
    }

    private void handleZoomOut(ActionEvent event) {
        Command command = new ZoomOutCommand(actionManager.getWorkspace());
        actionManager.executeCommand(command);
    }

    private void handleZoomReset(MouseEvent event) {
        applyZoom(1.0, null); // Zoom is not from scrolling; no pivot point needed, since scene center is
    }

    public void processEditorScrollStarted(ScrollEvent event) {
        if (editorModel.modeProperty().get() == EditorMode.IDLE_MODE) {
            editorModel.modeProperty().set(EditorMode.ZOOM_MODE);
        }
    }

    // Create and return the ScrollEvent handler for SCROLL
    public void processEditorScroll(ScrollEvent event) {

        boolean onMac = Config.get().operatingSystem() == SystemUtils.OperatingSystem.MACOS;
        boolean isZoomModeAndOnMac = editorModel.modeProperty().get() == EditorMode.ZOOM_MODE && onMac;
        boolean isIdleAndNotOnMac = editorModel.modeProperty().get() == EditorMode.IDLE_MODE && !onMac;

        Node intersectedNode = event.getPickResult().getIntersectedNode();
        boolean onScrollPane = NodeHierarchyUtils.isNodeOrParentOfType(intersectedNode, ScrollPane.class);
        boolean onListView = NodeHierarchyUtils.isNodeOrParentOfType(intersectedNode, ListView.class);

        if (!onScrollPane && !onListView && (isZoomModeAndOnMac || isIdleAndNotOnMac)) {

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
            applyZoom(newScale, pivotPoint);  // Zoom from scrolling; pass mouse position
        }
    }

    public void processEditorScrollFinished(ScrollEvent event) {
        if (editorModel.modeProperty().get() == EditorMode.ZOOM_MODE) {
            editorModel.modeProperty().set(EditorMode.IDLE_MODE);
        }
    }

    // Apply zoom and adjust pivot to keep zoom centered
    private void applyZoom(double newScale, Point2D pivotPoint) {
        Command command = new ApplyZoomCommand(actionManager.getWorkspace(), newScale, pivotPoint);
        actionManager.executeCommand(command);
    }

}
