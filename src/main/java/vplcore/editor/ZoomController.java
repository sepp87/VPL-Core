package vplcore.editor;

import javafx.event.ActionEvent;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import vplcore.Config;
import vplcore.Util;
import vplcore.util.NodeHierarchyUtils;
import vplcore.workspace.ActionManager;
import vplcore.workspace.Command;
import vplcore.workspace.EventRouter;
import vplcore.workspace.Workspace;
import vplcore.workspace.command.ApplyZoomCommand;
import vplcore.workspace.command.ZoomToFitCommand;

/**
 * Manages zooming functionality and controls in the workspace.
 */
public class ZoomController extends HBox {

    private final ActionManager actionManager;
    private final ZoomModel model;
    private final ZoomView view;
    private final Workspace workspace;

    // To throttle zoom on macOS
    private long lastZoomTime = 0;
    private final long zoomThrottleInterval = 50;  // Throttle time in milliseconds (tune for macOS)

    public ZoomController(ActionManager actionManager, ZoomModel zoomModel, Workspace workspace, ZoomView zoomView) {
        this.actionManager = actionManager;
        this.workspace = workspace;
        this.model = zoomModel;
        this.view = zoomView;

        view.getZoomInButton().setOnAction(this::handleZoomIn);
        view.getZoomOutButton().setOnAction(this::handleZoomOut);
        view.getZoomLabel().setOnMouseClicked(this::handleZoomReset);  // Reset zoom to 100% on click
        view.getZoomLabel().textProperty().bind(zoomModel.zoomFactorProperty().multiply(100).asString("%.0f%%"));

        workspace.setOnKeyPressed(this::handleZoomShortcuts);
    }

    public void incrementZoom() {
        handleZoomIn(null);
    }

    public void decrementZoom() {
        handleZoomOut(null);
    }

    private void handleZoomOut(ActionEvent event) {
        double newScale = model.getDecrementedZoomFactor();
        applyZoom(newScale); // Zoom is not from scrolling; no scroll event needed
    }

    private void handleZoomIn(ActionEvent event) {
        double newScale = model.getIncrementedZoomFactor();
        applyZoom(newScale); // Zoom is not from scrolling; no scroll event needed
    }

    private void handleZoomReset(MouseEvent event) {
        applyZoom(1.0); // Zoom is not from scrolling; no scroll event needed
    }

    // Create and return the ScrollEvent handler for SCROLL
    public void processEditorScroll(ScrollEvent event) {

        Node intersectedNode = event.getPickResult().getIntersectedNode();
        boolean onScrollPane = NodeHierarchyUtils.isNodeOrParentOfType(intersectedNode, ScrollPane.class);
        boolean onListView = NodeHierarchyUtils.isNodeOrParentOfType(intersectedNode, ListView.class);

        if (!onScrollPane && !onListView) {

            // TODO multiplier used for smooth scrolling, not implemented
            double multiplier = Config.get().operatingSystem() == Util.OperatingSystem.WINDOWS ? 1.2 : 1.05;

            // Throttle zoom on macOS
            boolean onMac = Config.get().operatingSystem() == Util.OperatingSystem.MACOS;
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

    private void applyZoom(double newScale) {
        applyZoom(newScale, null);
    }

    // Apply zoom and adjust pivot to keep zoom centered
    private void applyZoom(double newScale, Point2D pivotPoint) {
        Command command = new ApplyZoomCommand(actionManager.getWorkspace(), newScale, pivotPoint);
        actionManager.executeCommand(command);
    }

    public void zoomToFit() {
        Command command = new ZoomToFitCommand(actionManager.getWorkspace());
        actionManager.executeCommand(command);
    }

    private void handleZoomShortcuts(KeyEvent event) {

        // Handle keyboard shortcuts for zooming
        if (Util.isModifierDown(event)) {
            Double newScale = null;
            if (event.getCode() == KeyCode.PLUS) {
                newScale = model.getIncrementedZoomFactor();
            } else if (event.getCode() == KeyCode.MINUS) {
                newScale = model.getDecrementedZoomFactor();
            }
            if (newScale != null) {
                applyZoom(newScale); // Zoom is not from scrolling; no scroll event needed
            }
        } else if (event.getCode() == KeyCode.SPACE) {
            zoomToFit();
        }
    }

}
