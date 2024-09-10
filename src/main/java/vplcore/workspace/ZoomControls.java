package vplcore.workspace;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import vplcore.Config;
import vplcore.Util;
import static vplcore.workspace.Workspace.clamp;

/**
 *
 * @author joostmeulenkamp
 */
public class ZoomControls extends HBox {

    private final Workspace workspace;
    private final Label zoomLabel;  // Label to show zoom percentage
    private final Button zoomInButton;  // Button to zoom in
    private final Button zoomOutButton;  // Button to zoom out
    private final DoubleProperty zoomFactor;  // Property to hold zoom factor

    public ZoomControls(Workspace workspace) {
        this.workspace = workspace;

        zoomFactor = new SimpleDoubleProperty(1.0);  // Default zoom level is 100%

        // Label to display current zoom factor
        zoomLabel = new Label(getFormattedZoom());
        zoomLabel.getStyleClass().add("zoom-label");
        zoomLabel.setOnMouseClicked(e -> resetZoom());  // Reset zoom to 100% on click

        // Zoom Out Button
        zoomOutButton = new Button("-");
        zoomOutButton.setOnAction(e -> decrementZoom());
        zoomOutButton.getStyleClass().add("zoom-button");

        // Zoom In Button
        zoomInButton = new Button("+");
        zoomInButton.setOnAction(e -> incrementZoom());
        zoomInButton.getStyleClass().add("zoom-button");

        // Add elements to the HBox layout
        setSpacing(10);
        setPadding(new Insets(10));
        setAlignment(Pos.CENTER_LEFT);
        getStyleClass().add("zoom-controls");

        getChildren().addAll(zoomLabel, zoomOutButton, zoomInButton);

        // Add keyboard shortcuts for zoom
        setOnKeyPressed(this::handleKeyPress);

        // Bind the zoom label to update whenever the zoom factor changes
        zoomFactor.addListener((observable, oldValue, newValue) -> zoomLabel.setText(getFormattedZoom()));
    }

    // Method to format the zoom factor as a percentage string
    private String getFormattedZoom() {
        return String.format("%.0f%%", zoomFactor.get() * 100);
    }

    // Method to increment zoom factor by the defined step size
    public void incrementZoom() {
        if (zoomFactor.get() < Workspace.MAX_ZOOM) {
            zoomFactor.set(Math.min(Workspace.MAX_ZOOM, zoomFactor.get() + Workspace.ZOOM_STEP));
            setPivot(zoomFactor.get());

            workspace.setScale(zoomFactor.get());

        }
    }

    // Method to decrement zoom factor by the defined step size
    public void decrementZoom() {
        if (zoomFactor.get() > Workspace.MIN_ZOOM) {
            zoomFactor.set(Math.max(Workspace.MIN_ZOOM, zoomFactor.get() - Workspace.ZOOM_STEP));
            setPivot(zoomFactor.get());

            workspace.setScale(zoomFactor.get());

        }
    }

    // Method to reset zoom to the default 100%
    public void resetZoom() {
        zoomFactor.set(1.0);
        setPivot(1.0);
        workspace.setScale(1.0);

    }

    // Handle keyboard shortcuts for zooming
    private void handleKeyPress(KeyEvent event) {
        if (event.isControlDown()) {
            if (event.getCode() == KeyCode.PLUS || event.getCode() == KeyCode.EQUALS) {
                incrementZoom();
            } else if (event.getCode() == KeyCode.MINUS) {
                decrementZoom();
            }
        }
    }

    // Public getter for zoom factor property
    public DoubleProperty zoomFactorProperty() {
        return zoomFactor;
    }

    private void setPivot(double scaleFactor) {
        // Get the current scale of the workspace (assumed to be the same for both X and Y)
        double oldScale = workspace.getScale();

        // Calculate the scaling factor difference
        double scaleChange = (scaleFactor / oldScale) - 1;

        // Get the bounds of the zoom pane in the workspace's parent coordinates
        Bounds zoomPaneBounds = workspace.localToParent(workspace.zoomPane.getBoundsInParent());

        // Calculate the center of the scene (visible area)
        double sceneCenterX = workspace.getScene().getWidth() / 2;
        double sceneCenterY = workspace.getScene().getHeight() / 2;

        // Calculate the distance from the zoom pane's center to the scene's center
        double dx = (sceneCenterX - (zoomPaneBounds.getWidth() / 2 + zoomPaneBounds.getMinX()));
        double dy = (sceneCenterY - (zoomPaneBounds.getHeight() / 2 + zoomPaneBounds.getMinY()));

        // Calculate the new translation needed to zoom to the center
        double newTranslateX = scaleChange * dx;
        double newTranslateY = scaleChange * dy;
        workspace.setPivot(newTranslateX, newTranslateY);
    }

}
