package vplcore.workspace.input;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * Manages zooming functionality and controls in the workspace.
 */
public class ZoomView extends HBox {

    private final Label zoomLabel;  // Label to show zoom percentage
    private final Button zoomInButton;  // Button to zoom in
    private final Button zoomOutButton;  // Button to zoom out

    public ZoomView(ZoomModel zoomModel) {

        // Initialize UI components
        zoomLabel = new Label();
        zoomLabel.getStyleClass().add("zoom-label");
        zoomLabel.textProperty().bind(zoomModel.zoomFactorProperty().multiply(100).asString("%.0f%%"));

        zoomOutButton = new Button("-");
        zoomOutButton.getStyleClass().add("zoom-button");

        zoomInButton = new Button("+");
        zoomInButton.getStyleClass().add("zoom-button");

        // Set up the HBox layout
        setSpacing(10);
        setPadding(new Insets(10));
        setAlignment(Pos.CENTER_LEFT);
        getStyleClass().add("zoom-controls");

        getChildren().addAll(zoomLabel, zoomOutButton, zoomInButton);
    }
    
    public Button getZoomInButton() {
        return zoomInButton;
    }
    
    public Button getZoomOutButton() {
        return zoomOutButton;
    }
    
    public Label getZoomLabel() {
        return zoomLabel;
    }

}
