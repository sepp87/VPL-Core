package vplcore.graph.model;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.layout.AnchorPane;

/**
 *
 * @author joostmeulenkamp
 */
public class BlockException extends VBox {

    // has exception?
    // isminimized?
    // list of exceptions with index to item causing exception
    // page between exceptions
    // minimize exception
    // maximize exception
    // hide exception
//    private final List<Exception> exceptions = new ArrayList<>();
    private final List<String> exceptions = new ArrayList<>();
    private int currentIndex = 0;
    private final Label messageLabel;
    private final Label pageLabel;
    private final Button prevButton, nextButton;
    private final Button minimizeButton;
    private final Button maximizeButton;
    private final VBox maximizedBox;
    private final HBox navigationBox;

    private boolean resizing = false;

    public BlockException() {
        this.maximizedBox = new VBox(10);

        // Initialize UI components
        this.messageLabel = new Label();
        messageLabel.getStyleClass().add("block-exception-message");
        messageLabel.setWrapText(true);
        this.pageLabel = new Label();
        pageLabel.setAlignment(Pos.CENTER);

        // Create paging buttons
        this.prevButton = new Button("<");
        prevButton.getStyleClass().add("block-exception-navigation-button");
        this.nextButton = new Button(">");
        nextButton.getStyleClass().add("block-exception-navigation-button");

        // Set navigation button handlers
        prevButton.setOnAction(e -> showPreviousException());
        nextButton.setOnAction(e -> showNextException());
        nextButton.setAlignment(Pos.CENTER_RIGHT);

        // Navigation HBox
        this.navigationBox = new HBox(10, prevButton, pageLabel, nextButton);
        navigationBox.getStyleClass().add("block-exception-navigation-box");
//        navigationBox.prefWidthProperty().bind(maximizedBox.prefWidthProperty());

        // Minimize and maximize buttons
        this.minimizeButton = new Button("Minimize");
        minimizeButton.setOnAction(e -> minimize());
        minimizeButton.getStyleClass().add("block-exception-minimize-button");

        this.maximizeButton = new Button("Show Errors");
        maximizeButton.getStyleClass().add("block-exception-maximize-button");
        maximizeButton.setOnAction(e -> maximize());

        // Maximized state VBox
        maximizedBox.setPrefWidth(300);
        maximizedBox.getChildren().addAll(minimizeButton, messageLabel, navigationBox);
        maximizedBox.getStyleClass().add("block-exception-maximized-box");

        // Initially minimized state
        getChildren().add(maximizeButton);

        // Apply VBox layout priorities for responsive resizing
        VBox.setVgrow(messageLabel, Priority.ALWAYS);

        // Apply CSS styling to the root VBox
        this.getStyleClass().add("block-exception");

//        maximizedBox.heightProperty().addListener(cl);
        this.heightProperty().addListener(cl);
        
//        AnchorPane.setBottomAnchor(this, 10.0);  // Align the whole panel to the bottom of its container
//        AnchorPane.setLeftAnchor(this, 10.0);
//        this.layoutXProperty().addListener(cl);
//        this.layoutYProperty().addListener(cl);
    }

    ChangeListener<Object> cl = this::handleExceptionShown;

    private void handleExceptionShown(Object b, Object o, Object n) {

//        height = maximizedBox.getHeight();
        height = this.getHeight();
        System.out.println("Height " + height + "   previousHeight " + previousHeight);
//        System.out.println(this.getPrefHeight() + " : " + this.getHeight());
//        updateLayout();
    }

    private double previousHeight;
    private double height;

    private void updateLayout() {
        double y = BlockException.this.getLayoutY();
        double dY = previousHeight - height;
        double lY = y + dY;
        System.out.println("LayoutY " + y + " dY " + dY + " newLayoutY " + lY);
//        System.out.println("dY " + dY + " LayoutY " + lY);
        BlockException.this.setLayoutY(lY);
        this.layout();
    }

    // Set the exceptions to be shown in the panel
//    public void setExceptions(List<Exception> exceptionList) {
    public void setExceptions(List<String> exceptionList) {
        this.exceptions.clear();
        this.exceptions.addAll(exceptionList);
        this.currentIndex = 0;

        // Update UI state
        if (!exceptions.isEmpty()) {
            minimize();  // Show the red button in minimized state
        }
        updateUI();
    }

    // Method to minimize the panel
    private void minimize() {
        getChildren().clear();
        getChildren().add(maximizeButton);
    }

    // Method to maximize the panel
    public void maximize() {
        getChildren().clear();
        getChildren().add(maximizedBox);
        updateUI();
    }

    // Update UI to reflect the current exception and pagination
    private void updateUI() {
//        previousHeight = maximizedBox.getHeight();
        previousHeight = this.getHeight();

        if (!exceptions.isEmpty()) {
//            messageLabel.setText(exceptions.get(currentIndex).getMessage());
            messageLabel.setText(exceptions.get(currentIndex));
            pageLabel.setText((currentIndex + 1) + " of " + exceptions.size());

            prevButton.setDisable(exceptions.size() == 1);
            nextButton.setDisable(exceptions.size() == 1);
        } else {
            removePanel();
        }

    }

    // Show the previous exception in the list
    private void showPreviousException() {
        System.out.println("currentIndex " + currentIndex);
        if (currentIndex > 0) {
            currentIndex--;
        } else {
            currentIndex = exceptions.size() - 1;
        }
        updateUI();
    }

    // Show the next exception in the list
    private void showNextException() {
        System.out.println("currentIndex " + currentIndex);
        if (currentIndex < exceptions.size() - 1) {
            currentIndex++;
        } else {
            currentIndex = 0;
        }
        updateUI();
    }

    // Remove the panel when no exceptions remain
    private void removePanel() {
        getChildren().clear();
    }
}
