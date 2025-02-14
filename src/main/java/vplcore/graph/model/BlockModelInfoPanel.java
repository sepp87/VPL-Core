package vplcore.graph.model;

import java.util.List;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import vplcore.FontAwesomeIcon;
import vplcore.graph.util.MethodBlockModel;
import vplcore.workspace.BlockModel;
import vplcore.workspace.BlockView;
import vplcore.workspace.PortModel;
import vplcore.workspace.WorkspaceView;

/**
 *
 * @author Joost
 */
public class BlockModelInfoPanel extends Pane {

    // specify types info, warning and error
    // style panel by severity 
    protected final WorkspaceView workspaceView;
    protected final BlockModel blockModel;
    protected final BlockView blockView;

    protected Button closeButton;
    protected VBox infoBubble;
    protected Path tail;
    protected ScrollPane messagePane;

    public static final double MAX_HEIGHT = 420;

    public BlockModelInfoPanel(BlockModel blockModel) {
        this.workspaceView = blockModel.workspaceController.getView();
        this.blockModel = blockModel;
        this.blockView = blockModel.workspaceController.getBlockController(blockModel).getView();

        VBox container = new VBox(-2);
        container.setPrefHeight(MAX_HEIGHT);
        container.setAlignment(Pos.BOTTOM_LEFT);

        // create info bubble with tail
        this.infoBubble = buildInfoBubble();
        this.tail = buildTail();

        // add info bubble and tail to panel
        this.getStyleClass().add("block-info");
        container.getChildren().addAll(infoBubble, tail);
        this.getChildren().add(container);

        setPosition();

        workspaceView.getChildren().addFirst(this);
    }

    protected void setPosition() {
        double layoutX = blockView.layoutXProperty().get() + blockView.getWidth() - 63;
        double layoutY = blockView.layoutYProperty().get() - MAX_HEIGHT + 25;
        this.setLayoutX(layoutX);
        this.setLayoutY(layoutY);
    }

    private VBox buildInfoBubble() {

        // create title box
        this.closeButton = new Button(FontAwesomeIcon.TIMES.unicode());
        closeButton.getStyleClass().add("block-info-close-button");
        closeButton.setOnAction(e -> delete());
        HBox titleBox = new HBox(closeButton);
        titleBox.setAlignment(Pos.CENTER_RIGHT);

        // create content
        VBox content = buildContent();

        // create scroll pane to contain info message
        this.messagePane = new ScrollPane(content);
        messagePane.setFitToWidth(true);
        messagePane.setFocusTraversable(false);
        messagePane.setOnMousePressed(event -> workspaceView.requestFocus());
        messagePane.prefViewportHeightProperty().bind(content.heightProperty());

        // create info bubble
        VBox infoBubble = new VBox();
        infoBubble.getStyleClass().add("block-info-bubble");
        double offsetX = -18; // 18.46 instead of 40

        infoBubble.setPrefWidth(220);
        infoBubble.setMaxHeight(Double.MAX_VALUE);

        infoBubble.setLayoutX(offsetX);
        infoBubble.getChildren().addAll(titleBox, messagePane);

        return infoBubble;
    }

    protected VBox buildContent() {

        VBox content = new VBox();

        // create headers
        Label descriptionHeader = buildHeader("DESCRIPTION");
        Label inputHeader = buildHeader("INPUT");
        Label outputHeader = buildHeader("OUTPUT");

        // create content
        Label description = buildDescription();
        Label input = buildInput();
        Label output = buildOutput();

//        content.getChildren().addAll(descriptionHeader, description, inputHeader, input, outputHeader, output);
        content.getChildren().addAll(description, inputHeader, input, outputHeader, output);
        return content;
    }

    protected Label buildHeader(String header) {
        Label label = new Label(header);
        label.getStyleClass().add("header");
        return label;
    }

    private Label buildDescription() {
        BlockMetadata info;
        if (blockModel instanceof MethodBlockModel methodBlock) {
            info = methodBlock.method.getAnnotation(BlockMetadata.class);
        } else {
            info = blockModel.getClass().getAnnotation(BlockMetadata.class);
        }
        String description = info.description().isEmpty() ? "n/a" : info.description();
        Label label = buildLabel(description);

        return label;
    }

    protected Label buildLabel(String text) {
        Label label = new Label(text);
        label.setWrapText(true);
        return label;
    }

    private Label buildInput() {
        Label label = buildPortsDescription(blockModel.getInputPorts());
        return label;
    }

    private Label buildOutput() {
        Label label = buildPortsDescription(blockModel.getOutputPorts());
        return label;
    }

    private Label buildPortsDescription(List<PortModel> ports) {
        Label label = new Label();
        if (ports.isEmpty()) {
            label.setText("n/a");
            return label;
        }
        String result = "";
        for (PortModel port : ports) {
            result += port.getName() + " : " + port.dataType.getSimpleName() + "\n";

        }
        result = result.substring(0, result.length() - 1);
        label.setText(result);
        return label;
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
//        double degrees = 37.5;

        double degrees = 30;
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
//        VBox.setMargin(tail, new Insets(0, 0, 0, 40));
        tail.setTranslateX(40);
        tail.getElements().add(new MoveTo(bottomLeft.getX(), bottomLeft.getY()));
        tail.getElements().add(new LineTo(bottomRight.getX(), bottomRight.getY()));
        tail.getElements().add(new LineTo(intersection1.getX(), intersection1.getY()));
        tail.getElements().add(new ArcTo(radius, radius, 0, intersection2.getX(), intersection2.getY(), false, true));
        tail.getElements().add(new ClosePath());

        tail.getStyleClass().add("block-info-tail");
        return tail;
    }

    public void delete() {
        workspaceView.getChildren().remove(BlockModelInfoPanel.this);
        closeButton.setOnAction(null);
        blockView.removeInfoPanel();
        messagePane.setOnMousePressed(null);
        // remove block info panel
        // remove block port labels
    }

    public void move(double dX, double dY) {
        double layoutX = this.getLayoutX();
        double layoutY = this.getLayoutY();
        this.setLayoutX(layoutX + dX);
        this.setLayoutY(layoutY + dY);
    }

}
