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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import vplcore.Config;
import vplcore.Util;
import vplcore.workspace.Workspace;

/**
 * Manages zooming functionality and controls in the workspace.
 */
public class ZoomManager extends HBox {

    private final Workspace workspace;
    private final Label zoomLabel;  // Label to show zoom percentage
    private final Button zoomInButton;  // Button to zoom in
    private final Button zoomOutButton;  // Button to zoom out
    private final DoubleProperty zoomFactor;  // Property to hold zoom factor

    // Scene initialization handler
    private final ChangeListener<Object> initializationHandler = createInitializationHandler();

    // Scroll event handlers
    private final EventHandler<ScrollEvent> scrollStartedHandler = createScrollStartedHandler();
    private final EventHandler<ScrollEvent> scrollHandler = createScrollHandler();
    private final EventHandler<ScrollEvent> scrollFinishedHandler = createScrollFinishedHandler();

    // Button event handlers
    private final EventHandler<ActionEvent> decrementZoomHandler = createDecrementZoomHandler();
    private final EventHandler<ActionEvent> incrementZoomHandler = createIncrementZoomHandler();
    private final EventHandler<MouseEvent> resetZoomHandler = createResetZoomHandler();

    public ZoomManager(Workspace workspace) {
        this.workspace = workspace;
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

        // Add keyboard shortcuts for zoom
        setOnKeyPressed(this::handleKeyPress);

        // Bind the zoom label to update whenever the zoom factor changes
        zoomFactor.addListener((observable, oldValue, newValue) -> zoomLabel.setText(getFormattedZoom()));

        // Add scroll event handlers as soon as scene is initialized
        this.sceneProperty().addListener(initializationHandler);

    }

    // Format the zoom factor as a percentage string
    private String getFormattedZoom() {
        return String.format("%.0f%%", zoomFactor.get() * 100);
    }

    // Handle keyboard shortcuts for zooming
    private void handleKeyPress(KeyEvent event) {
        if (event.isControlDown()) {
            if (event.getCode() == KeyCode.PLUS || event.getCode() == KeyCode.EQUALS) {
                zoomFactor.set(getNextZoomIncrement());
            } else if (event.getCode() == KeyCode.MINUS) {
                zoomFactor.set(getNextZoomDecrement());
            }
            applyZoom(null); // Zoom is not from scrolling; no scroll event needed
        }
    }
    // Increment zoom factor by the defined step size

    private double getNextZoomIncrement() {
        return Math.min(Workspace.MAX_ZOOM, zoomFactor.get() + Workspace.ZOOM_STEP);
    }

    // Decrement zoom factor by the defined step size
    private double getNextZoomDecrement() {
        return Math.max(Workspace.MIN_ZOOM, zoomFactor.get() - Workspace.ZOOM_STEP);
    }

    // Apply zoom and adjust pivot to keep zoom centered
    private void applyZoom(ScrollEvent event) {
        double scaleFactor = zoomFactor.get();
        double oldScale = workspace.getScale();
        double scaleChange = (scaleFactor / oldScale) - 1;

        // Get the bounds of the zoom pane in the workspace's parent coordinates
        Bounds zoomPaneBounds = workspace.localToParent(workspace.zoomPane.getBoundsInParent());

        double dx, dy;

        if (event != null) {
            // Calculate the distance from the zoom point (mouse cursor) to the center
            dx = (event.getSceneX() - (zoomPaneBounds.getWidth() / 2 + zoomPaneBounds.getMinX()));
            dy = (event.getSceneY() - (zoomPaneBounds.getHeight() / 2 + zoomPaneBounds.getMinY()));
        } else {
            // Calculate the center of the scene (visible area)
            double sceneCenterX = getScene().getWidth() / 2;
            double sceneCenterY = getScene().getHeight() / 2;

            // Calculate the distance from the zoom pane's center to the scene's center
            dx = (sceneCenterX - (zoomPaneBounds.getWidth() / 2 + zoomPaneBounds.getMinX()));
            dy = (sceneCenterY - (zoomPaneBounds.getHeight() / 2 + zoomPaneBounds.getMinY()));
        }

        // Calculate the new translation needed to zoom to the center or to the mouse position
        double newTranslateX = scaleChange * dx;
        double newTranslateY = scaleChange * dy;

        workspace.setPivot(newTranslateX, newTranslateY);
        workspace.setScale(scaleFactor);
    }

    private EventHandler<ScrollEvent> createScrollStartedHandler() {
        return (ScrollEvent event) -> {
            if (workspace.getMouseMode() == MouseMode.MOUSE_IDLE) {
                workspace.setMouseMode(MouseMode.ZOOMING);
            }
        };
    }

    // Create and return the ScrollEvent handler for SCROLL
    private EventHandler<ScrollEvent> createScrollHandler() {
        return (ScrollEvent event) -> {
            boolean onWindows = Config.get().operatingSystem() == Util.OperatingSystem.WINDOWS;
            if (workspace.getMouseMode() == MouseMode.ZOOMING || onWindows) {

                // multiplier used for smooth scrolling, not implemented
                double multiplier = Config.get().operatingSystem() == Util.OperatingSystem.WINDOWS ? 1.2 : 1.05;

                // Adjust zoom factor based on scroll direction
                if (event.getDeltaY() > 0) {
                    zoomFactor.set(getNextZoomIncrement());
                } else {
                    zoomFactor.set(getNextZoomDecrement());
                }
                applyZoom(event);  // Zoom from scrolling; pass mouse position
                event.consume();
            }
        };
    }

    // Create and return the ScrollEvent handler for SCROLL_FINISHED
    private EventHandler<ScrollEvent> createScrollFinishedHandler() {
        return (ScrollEvent event) -> {
            if (workspace.getMouseMode() == MouseMode.ZOOMING) {
                workspace.setMouseMode(MouseMode.MOUSE_IDLE);
            }
        };
    }

    private EventHandler<ActionEvent> createDecrementZoomHandler() {
        return (ActionEvent event) -> {
            zoomFactor.set(getNextZoomDecrement());
            applyZoom(null); // Zoom is not from scrolling; no scroll event needed
        };
    }

    private EventHandler<ActionEvent> createIncrementZoomHandler() {
        return (ActionEvent event) -> {
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
        getScene().addEventFilter(ScrollEvent.SCROLL_STARTED, scrollStartedHandler);
        getScene().addEventFilter(ScrollEvent.SCROLL, scrollHandler);
        getScene().addEventFilter(ScrollEvent.SCROLL_FINISHED, scrollFinishedHandler);
    }
}
