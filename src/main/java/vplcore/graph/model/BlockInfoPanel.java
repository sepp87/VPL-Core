package vplcore.graph.model;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
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

    private VBox infoBubble;
    private Label messageLabel;
    private Label pageLabel;
    private HBox pagingControls;

    private double previousInfoBubbleHeight;
    private double currentInfoBubbleHeight;
    ChangeListener<Object> infoMessageShownHandler = this::handleInfoMessageShown;

    public BlockInfoPanel(Workspace workspace) {
        this.workspace = workspace;
        
        // create info bubble with tail
        this.infoBubble = buildInfoBubble();
        Path tail = buildTail();

        // add info bubble and tail to panel
        this.setStyle("-fx-effect: dropshadow( gaussian , rgba(0,0,0,0.1) , 10,0,7,7 );");
        this.getStyleClass().add("block-info");
        this.getChildren().addAll(infoBubble, tail);
    }

    // Set the exceptions to be shown in the panel
    public void setMessages(List<String> messages) {
        this.messages.clear();
        this.messages.addAll(messages);
        this.currentIndex = 0;

        // Update UI state
        updateUI();
    }

    private VBox buildInfoBubble() {

        // create title box
        Button closeButton = new Button("X");
        closeButton.setOnAction(e -> remove());
        HBox titleBox = new HBox(closeButton);
//        titleBox.setAlignment(Pos.CENTER_RIGHT);

        // create info message
        this.messageLabel = new Label();
        messageLabel.getStyleClass().add("block-info-message");
        messageLabel.setWrapText(true);

        // create info message paging controls
        this.pagingControls = buildPagingControls();

        // create info bubble
        this.infoBubble = new VBox();
        infoBubble.setStyle("-fx-background-color: #5F5F5F;-fx-background-radius: 4;");
        double offsetX = -40;
//        double height = 220;
        infoBubble.setPrefWidth(220);

        infoBubble.heightProperty().addListener(infoMessageShownHandler);
//        infoBubble.setPrefHeight(height);
//        infoBubble.setLayoutY(-height);
        infoBubble.setLayoutX(offsetX);
        infoBubble.getChildren().addAll(titleBox, messageLabel, pagingControls);

        return infoBubble;
    }

    private void handleInfoMessageShown(Object b, Object o, Object n) {
        currentInfoBubbleHeight = this.infoBubble.getHeight();
        System.out.println("Height " + currentInfoBubbleHeight + "   previousHeight " + previousInfoBubbleHeight);
        double y = infoBubble.getLayoutY();
        double dY = previousInfoBubbleHeight - currentInfoBubbleHeight;
        double lY = y + dY;
        System.out.println("LayoutY " + y + " dY " + dY + " newLayoutY " + lY);
        infoBubble.setLayoutY(lY);
        this.layout();
    }

    private HBox buildPagingControls() {

        this.pageLabel = new Label("1 of 10");

        // Create paging buttons
        Button previousButton = new Button("<");
        previousButton.getStyleClass().add("block-info-navigation-button");
        Button nextButton = new Button(">");
        nextButton.getStyleClass().add("block-info-navigation-button");

        // Set navigation button handlers
        previousButton.setOnAction(e -> showPreviousMessage());
        nextButton.setOnAction(e -> showNextMessage());

        // Set navigation button handlers
//        nextButton.setAlignment(Pos.CENTER_RIGHT);

        HBox navigationBox = new HBox(previousButton, pageLabel, nextButton);
//        navigationBox.setAlignment(Pos.BOTTOM_RIGHT);
        return navigationBox;
    }

    private Path buildTail() {

        // because of the inverted Y-axis, the tail is mathematically described like a downward hill:
        // 
        // intersection2 |\ intersection1
        //               | \
        //    bottomLeft |__\ bottomRight
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
        Point2D bottomRight = new Point2D(overlap - tailHeight / slope, 0); // see above comment
        Point2D intersection1 = new Point2D(centerX, centerY + tailHeight).add(offset); // add the offset to find the intersection on the original declining line
        Point2D intersection2 = new Point2D(0, centerY + tailHeight);

        Path tail = new Path();
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
        System.out.println("currentIndex " + currentIndex);
        if (currentIndex > 0) {
            currentIndex--;
        } else {
            currentIndex = messages.size() - 1;
        }
        updateUI();
    }

    // Show the next exception in the list
    private void showNextMessage() {
        System.out.println("currentIndex " + currentIndex);
        if (currentIndex < messages.size() - 1) {
            currentIndex++;
        } else {
            currentIndex = 0;
        }
        updateUI();
    }

    // Update UI to reflect the current exception and pagination
    private void updateUI() {
        previousInfoBubbleHeight = infoBubble.getHeight();
        if (!messages.isEmpty()) {
            messageLabel.setText(messages.get(currentIndex));
            pageLabel.setText((currentIndex + 1) + " of " + messages.size());

        } else {
//            removePanel();
        }

    }
    
    private void remove() {
        workspace.getChildren().remove(this);
        // remove block info panel
        // remove block port labels
    }

}
