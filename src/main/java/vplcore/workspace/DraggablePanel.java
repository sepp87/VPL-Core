package vplcore.workspace;

import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.input.MouseEvent;

public class DraggablePanel extends Pane {

    private double offsetX;
    private double offsetY;

    private boolean infoPanelVisible = false;
    private boolean infoPanelExpanded = false;

    private Pane mainPanel;

    public DraggablePanel(Workspace workspace) {

        // Main Panel (200x200)
        mainPanel = new Pane();
        mainPanel.setPrefSize(200, 200);
        mainPanel.setStyle("-fx-background-color: lightblue; -fx-border-color: black; -fx-border-width: 1px;");

        // Info Panel (100x100, initially invisible)
        Pane infoPanel = new Pane();
        infoPanel.setPrefSize(100, 100);
        infoPanel.setStyle("-fx-background-color: lightgray; -fx-border-color: black; -fx-border-width: 1px;");
        infoPanel.setLayoutX(50); // Center it above the main panel
        infoPanel.setLayoutY(-110); // 10 pixels above the main panel
        infoPanel.setVisible(false);

        // Close Button (top-right corner of the main panel)
        Button closeButton = new Button("X");
        closeButton.setOnAction(e -> toggleInfoPanelVisibility(infoPanel));
        closeButton.setLayoutX(175); // Position the close button in the top-right corner
        closeButton.setLayoutY(5);

        // Toggle Height Button in Info Panel
        Button toggleHeightButton = new Button("Toggle Height");
        toggleHeightButton.setOnAction(e -> toggleInfoPanelHeight(infoPanel));
        toggleHeightButton.setLayoutX(10);
        toggleHeightButton.setLayoutY(10);
        infoPanel.getChildren().add(toggleHeightButton);

        // Add the close button to the main panel
        mainPanel.getChildren().add(closeButton);

        // Create a layout that holds both panels
        workspace.getChildren().addAll(mainPanel, infoPanel);

        // Enable dragging of the main panel
        makeDraggable(mainPanel, infoPanel);

    }

    // Toggle visibility of the info panel
    private void toggleInfoPanelVisibility(Pane infoPanel) {
        infoPanelVisible = !infoPanelVisible;
        infoPanel.setVisible(infoPanelVisible);
    }

    // Toggle the height of the info panel
    private void toggleInfoPanelHeight(Pane infoPanel) {
        infoPanelExpanded = !infoPanelExpanded;
        if (infoPanelExpanded) {
            infoPanel.setPrefHeight(200); // Expand to 200px height
            infoPanel.setLayoutY(mainPanel.getLayoutY() - 200 - 10); // Position it 10px above the main panel

        } else {
            infoPanel.setPrefHeight(100); // Collapse to 100px height
            infoPanel.setLayoutY(mainPanel.getLayoutY() - 100 - 10); // Position it 10px above the main panel

        }

    }

    // Make the main panel draggable, and move the info panel with it
    private void makeDraggable(Pane mainPanel, Pane infoPanel) {
        mainPanel.setOnMousePressed((MouseEvent event) -> {
            offsetX = event.getSceneX() - mainPanel.getLayoutX();
            offsetY = event.getSceneY() - mainPanel.getLayoutY();
        });

        mainPanel.setOnMouseDragged((MouseEvent event) -> {
            double newX = event.getSceneX() - offsetX;
            double newY = event.getSceneY() - offsetY;

            // Set new position of the main panel
            mainPanel.setLayoutX(newX);
            mainPanel.setLayoutY(newY);

            // Move the info panel along with the main panel
            infoPanel.setLayoutX(newX + 50); // Keep the info panel centered relative to main panel
            infoPanel.setLayoutY(newY - infoPanel.getHeight() - 10); // Position it 10px above the main panel
        });
    }

}
