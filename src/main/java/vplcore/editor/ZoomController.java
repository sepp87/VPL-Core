package vplcore.editor;

import javafx.event.ActionEvent;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import vplcore.Config;
import vplcore.Util;
import vplcore.graph.model.Block;
import vplcore.util.NodeHierarchyUtils;
import vplcore.workspace.Workspace;

/**
 * Manages zooming functionality and controls in the workspace.
 */
public class ZoomController extends HBox {

    private final ZoomModel model;
    private final ZoomView view;
    private final Workspace workspace;

    // To throttle zoom on macOS
    private long lastZoomTime = 0;
    private final long zoomThrottleInterval = 50;  // Throttle time in milliseconds (tune for macOS)

    public ZoomController(ZoomModel zoomModel, Workspace workspace, ZoomView zoomView) {
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
        workspace.requestFocus(); // Discard focus so spacebar does not trigger this action again
        double newScale = model.getDecrementedZoomFactor();
        applyZoom(newScale); // Zoom is not from scrolling; no scroll event needed
    }

    private void handleZoomIn(ActionEvent event) {
        workspace.requestFocus(); // Discard focus so spacebar does not trigger this action again
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
        double oldScale = model.zoomFactorProperty().get();
        double scaleChange = (newScale / oldScale) - 1;

        // Get the bounds of the workspace
        Bounds workspaceBounds = workspace.getBoundsInParent();

        double dx, dy;

        if (pivotPoint != null) {
            // Calculate the distance from the zoom point (mouse cursor/graph center) to the workspace origin
            dx = pivotPoint.getX() - workspaceBounds.getMinX();
            dy = pivotPoint.getY() - workspaceBounds.getMinY();
        } else {
            // Calculate the center of the scene (visible area)
            double sceneCenterX = view.getScene().getWidth() / 2;
            double sceneCenterY = view.getScene().getHeight() / 2;

            // Calculate the distance from the workspace's center to the scene's center
            dx = sceneCenterX - workspaceBounds.getMinX();
            dy = sceneCenterY - workspaceBounds.getMinY();
        }

        // Calculate the new translation needed to zoom to the center or to the mouse position
        double dX = scaleChange * dx;
        double dY = scaleChange * dy;

        double newTranslateX = model.translateXProperty().get() - dX;
        double newTranslateY = model.translateYProperty().get() - dY;

        model.translateXProperty().set(newTranslateX);
        model.translateYProperty().set(newTranslateY);
        model.zoomFactorProperty().set(newScale);
    }

    public void zoomToFit() {
        Scene scene = view.getScene();
        if (workspace.blockSet.isEmpty()) {
            return;
        }

        //Zoom to fit        
        Bounds boundingBox = workspace.localToParent(Block.getBoundingBoxOfBlocks(workspace.blockSet));
        double ratioX = boundingBox.getWidth() / scene.getWidth();
        double ratioY = boundingBox.getHeight() / scene.getHeight();
        double ratio = Math.max(ratioX, ratioY);
        // multiply, round and divide by 10 to reach zoom step of 0.1 and substract by 1 to zoom a bit more out so the blocks don't touch the border
        double scale = Math.ceil((model.zoomFactorProperty().get() / ratio) * 10 - 1) / 10;
        scale = scale < ZoomModel.MIN_ZOOM ? ZoomModel.MIN_ZOOM : scale;
        scale = scale > ZoomModel.MAX_ZOOM ? ZoomModel.MAX_ZOOM : scale;
        model.zoomFactorProperty().set(scale);

        //Pan to fit
        boundingBox = workspace.localToParent(Block.getBoundingBoxOfBlocks(workspace.blockSet));
        double dx = (boundingBox.getMinX() + boundingBox.getWidth() / 2) - scene.getWidth() / 2;
        double dy = (boundingBox.getMinY() + boundingBox.getHeight() / 2) - scene.getHeight() / 2;
        double newTranslateX = model.translateXProperty().get() - dx;
        double newTranslateY = model.translateYProperty().get() - dy;

        model.translateXProperty().set(newTranslateX);
        model.translateYProperty().set(newTranslateY);
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
