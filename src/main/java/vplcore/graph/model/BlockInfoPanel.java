package vplcore.graph.model;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import vplcore.workspace.Workspace;

/**
 *
 * @author Joost
 */
public class BlockInfoPanel extends Pane {

    private final Workspace workspace;

    private final List<String> messages = new ArrayList<>();
    private int currentIndex = 0;

    private Button closeButton;
    private Label messageLabel;
    private ScrollPane messagePane;

    private HBox pagingControls;
    private Label pageLabel;
    private Button nextButton;
    private Button previousButton;

    public BlockInfoPanel(Workspace workspace) {
        this.workspace = workspace;

        double height = 420;
        VBox container = new VBox(-2);
        container.setPrefHeight(height);
        container.setAlignment(Pos.BOTTOM_LEFT);
        container.setLayoutY(-height);
        container.setStyle("-fx-background-color: #FF0000;");

        // create info bubble with tail
        VBox infoBubble = buildInfoBubble();
        Path tail = buildTail();
        

        // add info bubble and tail to panel
        this.setStyle("-fx-effect: dropshadow( gaussian , rgba(0,0,0,0.1) , 10,0,7,7 );");
        this.getStyleClass().add("block-info");
        container.getChildren().addAll(infoBubble, tail);
        this.getChildren().add(container);
    }

    // Set the exceptions to be shown in the panel
    public void setMessages(List<String> messages) {
        this.messages.clear();
        this.messages.addAll(messages);
        this.currentIndex = 0;

        // Update UI state
        updateLabels();
    }

    private ScrollPane buildInfoBubble2() {

        // create title box
        this.closeButton = new Button("X");
        closeButton.setOnAction(e -> remove());
        HBox titleBox = new HBox(closeButton);
        titleBox.setAlignment(Pos.CENTER_RIGHT);

        // create info message
        this.messageLabel = new Label();
        messageLabel.getStyleClass().add("block-info-message");
        messageLabel.setWrapText(true);

        // create scroll pane to contain info message
        this.messagePane = new ScrollPane(messageLabel);
        messagePane.setFitToWidth(true);

        messagePane.setStyle("-fx-background-color: #5F5F5F;-fx-background-radius: 4;");
        double offsetX = -18; // 18.46 instead of 40 TODO is not added because of the alignment of container

        messagePane.setPrefWidth(220);
        messagePane.prefViewportHeightProperty().bind(messageLabel.heightProperty());

        messagePane.setLayoutX(offsetX);

        return messagePane;
    }

    private VBox buildInfoBubble() {

        // create title box
        this.closeButton = new Button("X");
        closeButton.setOnAction(e -> remove());
        HBox titleBox = new HBox(closeButton);
        titleBox.setAlignment(Pos.CENTER_RIGHT);

        // create info message
        this.messageLabel = new Label();
        messageLabel.getStyleClass().add("block-info-message");
        messageLabel.setWrapText(true);

        // create scroll pane to contain info message
        this.messagePane = new ScrollPane(messageLabel);
        messagePane.setFitToWidth(true);
        messagePane.prefViewportHeightProperty().bind(messageLabel.heightProperty());

        // create info message paging controls
        this.pagingControls = buildPagingControls();

        // create info bubble
        VBox infoBubble = new VBox();
        infoBubble.setStyle("-fx-background-color: #5F5F5F;-fx-background-radius: 4;");
        double offsetX = -18; // 18.46 instead of 40

        infoBubble.setPrefWidth(220);
        infoBubble.setMaxHeight(Double.MAX_VALUE);

        infoBubble.setLayoutX(offsetX);
        infoBubble.getChildren().addAll(titleBox, messagePane, pagingControls);

        return infoBubble;
    }

    private HBox buildPagingControls() {

        this.pageLabel = new Label("1 of 10");

        // Create paging buttons
        this.previousButton = new Button("<");
        previousButton.getStyleClass().add("block-info-navigation-button");
        this.nextButton = new Button(">");
        nextButton.getStyleClass().add("block-info-navigation-button");

        // Set navigation button handlers
        previousButton.setOnAction(e -> showPreviousMessage());
        nextButton.setOnAction(e -> showNextMessage());

        HBox navigationBox = new HBox(previousButton, pageLabel, nextButton);
        return navigationBox;
    }

    private Path buildTail() {

        // because of the inverted Y-axis, the tail is mathematically described like a downward hill:
        // 
        // intersection2 |\ intersection1
        //               | \
        //    bottomLeft |__\ bottomRight
        //
        double tailHeight = 30; // 30.1
        double radius = 3; // 4.99
        double degrees = 37.5;
        double radians = Math.toRadians(degrees);
        double slope = -Math.tan(radians); // invert the slope, since axis Y is pointing downward
        Point2D vector = new Point2D(-slope, 1);
        double length = vector.magnitude();
        Point2D normal = vector.multiply(1 / length);
        Point2D offset = normal.multiply(radius);
        double centerX = radius;
        double centerY = slope * radius + offset.getY();

        double overlap = -2;
        Point2D bottomLeft = new Point2D(0, overlap); // substract one pixel to overlap the tail with the info bubble to prevent a visual gap between both shapes
        Point2D bottomRight = new Point2D(overlap - tailHeight / slope, overlap); // see above comment
        Point2D intersection1 = new Point2D(centerX, centerY + tailHeight).add(offset); // add the offset to find the intersection on the original declining line
        Point2D intersection2 = new Point2D(0, centerY + tailHeight);

        Path tail = new Path();
        VBox.setMargin(tail, new Insets(0, 0, 0, 40));
        tail.getElements().add(new MoveTo(bottomLeft.getX(), bottomLeft.getY()));
        tail.getElements().add(new LineTo(bottomRight.getX(), bottomRight.getY()));
        tail.getElements().add(new LineTo(intersection1.getX(), intersection1.getY()));
        tail.getElements().add(new ArcTo(radius, radius, 0, intersection2.getX(), intersection2.getY(), false, true));
        tail.getElements().add(new ClosePath());

        tail.setStyle("-fx-fill: #5F5F5F;-fx-stroke: transparent;-fx-stroke-width: 0px;");
        return tail;
    }

    // Show the previous exception in the list
    private void showPreviousMessage() {
        if (currentIndex > 0) {
            currentIndex--;
        } else {
            currentIndex = messages.size() - 1;
        }
        updateLabels();
    }

    // Show the next exception in the list
    private void showNextMessage() {
        if (currentIndex < messages.size() - 1) {
            currentIndex++;
        } else {
            currentIndex = 0;
        }
        updateLabels();
    }

    // Update UI to reflect the current exception and pagination
    private void updateLabels() {
        messageLabel.setText(messages.get(currentIndex));
        pageLabel.setText((currentIndex + 1) + " of " + messages.size());
        messagePane.layout(); // Force scroll pane to recompute viewport height
    }

    private void remove() {
        workspace.getChildren().remove(BlockInfoPanel.this);
        closeButton.setOnAction(null);
        previousButton.setOnAction(null);
        nextButton.setOnAction(null);
        // remove block info panel
        // remove block port labels
    }

}
