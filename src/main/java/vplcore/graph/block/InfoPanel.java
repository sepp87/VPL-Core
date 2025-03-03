package vplcore.graph.block;

import java.util.ArrayList;
import java.util.Collections;
import vplcore.graph.port.PortModel;
import java.util.List;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
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
import vplcore.graph.util.MethodBlock;
import vplcore.workspace.WorkspaceView;

/**
 *
 * @author JoostMeulenkamp
 */
public class InfoPanel extends Pane {

    // specify types info, warning and error
    // style panel by severity 
    protected final WorkspaceView workspaceView;
    protected final BlockModel blockModel;
    protected final BlockView blockView;
    
    private final BooleanProperty removed = new SimpleBooleanProperty(this, "removed", false);

    protected Button closeButton;
    protected VBox infoBubble;
    protected Path tail;
    protected ScrollPane messagePane;

    private List<Label> inputs = Collections.emptyList();
    private List<Label> outputs = Collections.emptyList();

    public static final double MAX_HEIGHT = 420;

    public InfoPanel(WorkspaceView workspaceView, BlockController blockController) {
        this.workspaceView = workspaceView;
        this.blockModel = blockController.getModel();
        this.blockView = blockController.getView();

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

        container.setPickOnBounds(false);
        this.setPickOnBounds(false);

        setPosition();
    }
    
    public ReadOnlyBooleanProperty removedProperty(){
        return removed;
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
        closeButton.setOnAction(e -> remove());
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
        this.inputs = buildPortsDescriptionNew(blockModel.getInputPorts());
        this.outputs = buildPortsDescriptionNew(blockModel.getOutputPorts());

        // add to content container
        content.getChildren().addAll(description, inputHeader);
        content.getChildren().addAll(inputs);
        content.getChildren().addAll(outputHeader);
        content.getChildren().addAll(outputs);
        return content;
    }

    protected Label buildHeader(String header) {
        Label label = new Label(header);
        label.getStyleClass().add("header");
        return label;
    }

    private Label buildDescription() {
        BlockMetadata info;
        if (blockModel instanceof MethodBlock methodBlock) {
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

    private List<Label> buildPortsDescriptionNew(List<PortModel> ports) {
        List<Label> result = new ArrayList<>();

        if (ports.isEmpty()) {
            Label label = new Label();
            label.setText("n/a");
            result.add(label);
        }
        for (PortModel port : ports) {
            Label label = new Label();
            label.textProperty().bind(Bindings.createStringBinding(()
                    -> buildPortDescription(port), port.nameProperty(), port.dataTypeProperty()));
            result.add(label);
        }
        return result;
    }

    private String buildPortDescription(PortModel portModel) {
        return portModel.nameProperty().get() + " : " + portModel.getDataType().getSimpleName();
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

    public void remove() {
        removed.set(true);
//        workspaceView.getInfoLayer().getChildren().remove(InfoPanel.this);
        closeButton.setOnAction(null);
//        blockView.removeInfoPanel();
        messagePane.setOnMousePressed(null);

        for (Label label : inputs) {
            label.textProperty().unbind();
        }
        for (Label label : outputs) {
            label.textProperty().unbind();
        }
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
