package vplcore.workspace.input;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import vplcore.Config;
import vplcore.Util;
import vplcore.graph.model.Block;
import vplcore.workspace.Workspace;

/**
 * Manages zooming functionality and controls in the workspace.
 */
public class ZoomManager extends HBox {

    private static final double MAX_ZOOM = 1.5;
    private static final double MIN_ZOOM = 0.3;
    private static final double ZOOM_STEP = 0.1;

    private final DoubleProperty zoomFactor;  // Property to hold zoom factor

    // To throttle zoom on macOS
    private long lastZoomTime = 0;
    private final long zoomThrottleInterval = 50;  // Throttle time in milliseconds (tune for macOS)

    // UI elements
    private final Workspace workspace;
    private final Label zoomLabel;  // Label to show zoom percentage
    private final Button zoomInButton;  // Button to zoom in
    private final Button zoomOutButton;  // Button to zoom out

    // Scene initialization handler
    private final ChangeListener<Object> initializationHandler = createInitializationHandler();

    // Scroll event handlers
    private final EventHandler<ScrollEvent> scrollHandler = createScrollHandler();

    // Keyboard event Handler
    private final EventHandler<KeyEvent> keyEventHandler = createKeyEventHandler();

    // Button event handlers
    private final EventHandler<ActionEvent> decrementZoomHandler = createDecrementZoomHandler();
    private final EventHandler<ActionEvent> incrementZoomHandler = createIncrementZoomHandler();
    private final EventHandler<MouseEvent> resetZoomHandler = createResetZoomHandler();

    public ZoomManager(Workspace workspace) {
        this.workspace = workspace;
        workspace.zoomManager = this;

        this.zoomFactor = new SimpleDoubleProperty(1.0);  // Default zoom level is 100%

        // Initialize UI components
        zoomLabel = new Label(getFormattedZoom());
        zoomLabel.getStyleClass().add("zoom-label");
        zoomLabel.setOnMouseClicked(resetZoomHandler);  // Reset zoom to 100% on click

        zoomOutButton = new Button("-");
        zoomOutButton.setOnAction(decrementZoomHandler);
        zoomOutButton.getStyleClass().add("zoom-button");

        zoomInButton = new Button("+");
        zoomInButton.setOnAction(incrementZoomHandler);
        zoomInButton.getStyleClass().add("zoom-button");

        // Set up the HBox layout
        setSpacing(10);
        setPadding(new Insets(10));
        setAlignment(Pos.CENTER_LEFT);
        getStyleClass().add("zoom-controls");

        getChildren().addAll(zoomLabel, zoomOutButton, zoomInButton);

        // Bind the zoom label to update whenever the zoom factor changes
        zoomFactor.addListener((observable, oldValue, newValue) -> zoomLabel.setText(getFormattedZoom()));

        // Add scroll event handlers as soon as scene is initialized
        this.sceneProperty().addListener(initializationHandler);

    }

    // Format the zoom factor as a percentage string
    private String getFormattedZoom() {
        return String.format("%.0f%%", zoomFactor.get() * 100);
    }

    // Increment zoom factor by the defined step size
    private double getNextZoomIncrement() {
        return Math.min(MAX_ZOOM, zoomFactor.get() + ZOOM_STEP);
    }

    // Decrement zoom factor by the defined step size
    private double getNextZoomDecrement() {
        return Math.max(MIN_ZOOM, zoomFactor.get() - ZOOM_STEP);
    }

    public void incrementZoom() {
        zoomFactor.set(getNextZoomIncrement());
        applyZoom(null);
    }

    public void decrementZoom() {
        zoomFactor.set(getNextZoomDecrement());
        applyZoom(null);
    }

    // Apply zoom and adjust pivot to keep zoom centered
    private void applyZoom(ScrollEvent event) {
        double scaleFactor = zoomFactor.get();
        double oldScale = workspace.getScale();
        double scaleChange = (scaleFactor / oldScale) - 1;

        // Get the bounds of the workspace
        Bounds workspaceBounds = workspace.getBoundsInParent();

        double dx, dy;

        if (event != null) {
            // Calculate the distance from the zoom point (mouse cursor) to the center
            dx = event.getSceneX() - workspaceBounds.getMinX();
            dy = event.getSceneY() - workspaceBounds.getMinY();
        } else {
            // Calculate the center of the scene (visible area)
            double sceneCenterX = getScene().getWidth() / 2;
            double sceneCenterY = getScene().getHeight() / 2;

            // Calculate the distance from the workspace's center to the scene's center
            dx = sceneCenterX - workspaceBounds.getMinX();
            dy = sceneCenterY - workspaceBounds.getMinY();
        }

        // Calculate the new translation needed to zoom to the center or to the mouse position
        double newTranslateX = scaleChange * dx;
        double newTranslateY = scaleChange * dy;

        workspace.setPivot(newTranslateX, newTranslateY);
        workspace.setScale(scaleFactor);
    }

    // Create and return the ScrollEvent handler for SCROLL
    private EventHandler<ScrollEvent> createScrollHandler() {
        return (ScrollEvent event) -> {
            boolean onMac = Config.get().operatingSystem() == Util.OperatingSystem.MACOS;
            boolean onScrollPane = workspace.checkParents(event.getPickResult().getIntersectedNode(), ScrollPane.class);
            if (!onScrollPane) {

                // multiplier used for smooth scrolling, not implemented
                double multiplier = Config.get().operatingSystem() == Util.OperatingSystem.WINDOWS ? 1.2 : 1.05;

                // Throttle zoom on macOS
                if (onMac) {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastZoomTime < zoomThrottleInterval) {
                        return;  // Ignore if throttling is active
                    }
                    lastZoomTime = currentTime;  // Update the last zoom time
                }

                // Adjust zoom factor based on scroll direction
                if (event.getDeltaY() > 0) {
                    zoomFactor.set(getNextZoomIncrement());
                } else {
                    zoomFactor.set(getNextZoomDecrement());
                }
                applyZoom(event);  // Zoom from scrolling; pass mouse position
//                event.consume();
            }
        };
    }

    private EventHandler<KeyEvent> createKeyEventHandler() {
        return (KeyEvent keyEvent) -> {

            // Handle keyboard shortcuts for zooming
            if (Util.isModifierDown(keyEvent)) {
                if (keyEvent.getCode() == KeyCode.PLUS) {
                    zoomFactor.set(getNextZoomIncrement());
                } else if (keyEvent.getCode() == KeyCode.MINUS) {
                    zoomFactor.set(getNextZoomDecrement());
                }
                applyZoom(null); // Zoom is not from scrolling; no scroll event needed
            } else if (keyEvent.getCode() == KeyCode.SPACE) {
                zoomToFit();
            }
        };
    }

    private EventHandler<ActionEvent> createDecrementZoomHandler() {
        return (ActionEvent event) -> {
            workspace.requestFocus(); // Discard focus so spacebar does not trigger this action again
            zoomFactor.set(getNextZoomDecrement());
            applyZoom(null); // Zoom is not from scrolling; no scroll event needed
        };
    }

    private EventHandler<ActionEvent> createIncrementZoomHandler() {
        return (ActionEvent event) -> {
            workspace.requestFocus(); // Discard focus so spacebar does not trigger this action again
            zoomFactor.set(getNextZoomIncrement());
            applyZoom(null); // Zoom is not from scrolling; no scroll event needed
        };
    }

    private EventHandler<MouseEvent> createResetZoomHandler() {
        return (MouseEvent event) -> {
            zoomFactor.set(1.0); // Reset zoom to the default 100%
            applyZoom(null); // Zoom is not from scrolling; no scroll event needed
        };
    }

    private ChangeListener<Object> createInitializationHandler() {
        return (ObservableValue<? extends Object> b, Object o, Object n) -> {
            addInputHandlers();
        };
    }

    // Add scroll event handlers
    private void addInputHandlers() {
        getScene().addEventFilter(ScrollEvent.SCROLL, scrollHandler);
        getScene().addEventHandler(KeyEvent.KEY_PRESSED, keyEventHandler); // Add keyboard shortcuts for zoom
        workspace.requestFocus(); // Request focus, zoom to fit with SPACEBAR only works when workspace received focus
    }

    public void zoomToFit() {
        Scene scene = workspace.getScene();
        Bounds localBBox = Block.getBoundingBoxOfBlocks(workspace.blockSet);
        if (localBBox == null) {
            return;
        }

        //Zoom to fit        
        Bounds bBox = workspace.localToParent(localBBox);
        double ratioX = bBox.getWidth() / scene.getWidth();
        double ratioY = bBox.getHeight() / scene.getHeight();
        double ratio = Math.max(ratioX, ratioY);
        // multiply, round and divide by 10 to reach zoom step of 0.1 and substract by 1 to zoom a bit more out so the blocks don't touch the border
        double scale = Math.ceil((workspace.getScale() / ratio) * 10 - 1) / 10;
        scale = scale < MIN_ZOOM ? MIN_ZOOM : scale;
        scale = scale > MAX_ZOOM ? MAX_ZOOM : scale;
        zoomFactor.set(scale);
        workspace.setScale(scale);

        //Pan to fit
        bBox = workspace.localToParent(Block.getBoundingBoxOfBlocks(workspace.blockSet));
        double deltaX = (bBox.getMinX() + bBox.getWidth() / 2) - scene.getWidth() / 2;
        double deltaY = (bBox.getMinY() + bBox.getHeight() / 2) - scene.getHeight() / 2;
        workspace.setTranslateX(workspace.getTranslateX() - deltaX);
        workspace.setTranslateY(workspace.getTranslateY() - deltaY);
    }
}
