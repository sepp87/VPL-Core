package vplcore.graph.connection;

import java.io.File;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.SVGPath;
import jo.vpl.xml.ConnectionTag;
import vplcore.context.ActionManager;
import vplcore.context.command.RemoveConnectionCommand;
import vplcore.graph.port.PortModel;
import static vplcore.util.EventUtils.isLeftClick;
import vplcore.util.FileUtils;
import vplcore.workspace.WorkspaceController;
import vplcore.workspace.WorkspaceView;

/**
 *
 * @author JoostMeulenkamp
 */
public class ConnectionController {

    private final WorkspaceController workspaceController;
    private final WorkspaceView workspaceView;

    private final ConnectionModel model;
    private final ConnectionView view;


    private final ChangeListener<Object> portCoordinatesChangedListener = this::handlePortCoordinatesChanged;

    private final static double SNAPPING_WIDTH = 22;
    private final PortModel startPort;
    private final PortModel endPort;

    private final CubicCurve connectionCurve;
    private final CubicCurve snappingCurve;

    public final DoubleProperty startBezierXProperty = new SimpleDoubleProperty();
    public final DoubleProperty endBezierXProperty = new SimpleDoubleProperty();

    public ConnectionController(WorkspaceController workspaceController, ConnectionModel connectionModel, ConnectionView connectionView) {
        this.workspaceController = workspaceController;
        this.workspaceView = workspaceController.getView();
        this.model = connectionModel;
        this.view = connectionView;

        this.startPort = model.getStartPort();
        this.endPort = model.getEndPort();

        connectionCurve = view.getConnectionCurve();
        snappingCurve = view.getSnappingCurve();
        
        addChangeListeners();

        initializeRemoveButton();

    }

    private void addChangeListeners() {
        startPort.dataProperty().addListener(endPort.getStartPortDataChangedListener());

        // new listeners
        startPort.centerXProperty.addListener(portCoordinatesChangedListener);
        startPort.centerYProperty.addListener(portCoordinatesChangedListener);
        endPort.centerXProperty.addListener(portCoordinatesChangedListener);
        endPort.centerYProperty.addListener(portCoordinatesChangedListener);

        snappingCurve.setOnMouseMoved(this::handleMoveRemoveButton);
        snappingCurve.setOnMouseExited(this::handleHideRemoveButton);
        snappingCurve.setOnMouseEntered(this::handleShowRemoveButton);
    }

    private void handlePortCoordinatesChanged(ObservableValue ov, Object t, Object t1) {
        calculateBezierPoints();
    }

    private CubicCurve createCurve() {
        calculateBezierPoints();

        CubicCurve curve = new CubicCurve();
        curve.controlX1Property().bind(startBezierXProperty);
        curve.controlY1Property().bind(startPort.centerYProperty);
        curve.startXProperty().bind(startPort.centerXProperty);
        curve.startYProperty().bind(startPort.centerYProperty);

        curve.controlX2Property().bind(endBezierXProperty);
        curve.controlY2Property().bind(endPort.centerYProperty);
        curve.endXProperty().bind(endPort.centerXProperty);
        curve.endYProperty().bind(endPort.centerYProperty);

        return curve;
    }

    private void calculateBezierPoints() {
        Double dX = endPort.centerXProperty.get() - startPort.centerXProperty.get();
        Double dY = endPort.centerYProperty.get() - startPort.centerYProperty.get();
        Point2D vector = new Point2D(dX, dY);
        double distance = vector.magnitude() / 2;

        startBezierXProperty.set(startPort.centerXProperty.get() + distance);
        endBezierXProperty.set(endPort.centerXProperty.get() - distance);

    }

    public PortModel getStartPort() {
        return startPort;
    }

    public PortModel getEndPort() {
        return endPort;
    }


    public void remove() {
        removeChangeListeners();

        //Deactivate ports if they have no more connections
        if (startPort.connectedConnections.isEmpty()) {
            startPort.setActive(false);
        }

        if (endPort.connectedConnections.isEmpty()) {
            endPort.setActive(false);
        }

        endPort.calculateData();
        removeFromCanvas();

    }

    private void removeFromCanvas() {
//        workspaceController.removeChild(this);
        unbindCurve(connectionCurve);
        unbindCurve(snappingCurve);
        if (!endPort.multiDockAllowed) {
            endPort.parentBlock.onIncomingConnectionRemoved();
        }
    }

    private void unbindCurve(CubicCurve curve) {
        curve.controlX1Property().unbind();
        curve.controlY1Property().unbind();
        curve.startXProperty().unbind();
        curve.startYProperty().unbind();

        curve.controlX2Property().unbind();
        curve.controlY2Property().unbind();
        curve.endXProperty().unbind();
        curve.endYProperty().unbind();
    }

    private void removeChangeListeners() {
        startPort.dataProperty().removeListener(endPort.getStartPortDataChangedListener());

        startPort.centerXProperty.removeListener(portCoordinatesChangedListener);
        startPort.centerYProperty.removeListener(portCoordinatesChangedListener);
        endPort.centerXProperty.removeListener(portCoordinatesChangedListener);
        endPort.centerYProperty.removeListener(portCoordinatesChangedListener);

        snappingCurve.setOnMouseMoved(null);
        snappingCurve.setOnMouseExited(null);
        snappingCurve.setOnMouseEntered(null);

    }

    private static Group removeButton;
    private static SVGPath removeIcon;
    private static Circle circle;

    private void initializeRemoveButton() {

        if (removeButton != null) {
            if (!workspaceView.getChildren().contains(removeButton)) {
                workspaceView.getChildren().add(removeButton);
            }
            return;
        }

        String xml = FileUtils.readFileAsString(new File(vplcore.Config.get().iconsDirectory() + "circle-xmark-solid.svg"));
        String svg = xml.split("path d=\"")[1].replace("\"/></svg>", "");
        removeIcon = new SVGPath();
        removeIcon.setContent(svg);
        removeIcon.getStyleClass().add("connection-remove-icon");

        double width = removeIcon.prefWidth(-1);
        double height = removeIcon.prefHeight(-1);
        double desiredWidth = this.SNAPPING_WIDTH;
        double scale = desiredWidth / width;

        removeIcon.setLayoutX(-width / 2);
        removeIcon.setLayoutY(-height / 2);
        removeIcon.setScaleX(scale);
        removeIcon.setScaleY(scale);

        double radius = (desiredWidth - 1) / 2;
        circle = new Circle(0, 0, radius, Color.WHITE);

        removeButton = new Group();
        removeButton.getChildren().add(circle);
        removeButton.getChildren().add(removeIcon);
        removeButton.setVisible(false);
        removeButton.setMouseTransparent(true);
        workspaceView.getChildren().add(removeButton);
    }

    private void handleShowRemoveButton(MouseEvent event) {
        removeButton.setVisible(true);
        showRemoveButton(event);
        snappingCurve.addEventHandler(MouseEvent.MOUSE_CLICKED, clickedSnappingCurveHandler);
    }

    private void handleMoveRemoveButton(MouseEvent event) {
        showRemoveButton(event);
        event.consume();
    }

    private void handleHideRemoveButton(MouseEvent event) {
        Node node = event.getPickResult().getIntersectedNode();
        if (!node.equals(removeIcon) && !node.equals(circle)) {
            removeButton.setVisible(false);
            this.snappingCurve.removeEventHandler(MouseEvent.MOUSE_CLICKED, clickedSnappingCurveHandler);
        }
    }

    private final EventHandler<MouseEvent> clickedSnappingCurveHandler = new EventHandler<>() {
        @Override
        public void handle(MouseEvent event) {
            if (isLeftClick(event)) {
                ActionManager actionManager = workspaceController.getEditorContext().getActionManager();
                RemoveConnectionCommand command = new RemoveConnectionCommand(actionManager.getWorkspaceModel(), model);
                actionManager.executeCommand(command);
//                ConnectionModel.this.remove();
                removeButton.setVisible(false);
            }
        }
    };

    private void showRemoveButton(MouseEvent event) {

        Point2D mouse = workspaceView.sceneToLocal(event.getSceneX(), event.getSceneY());

        // Check proximity to the curve using parameter t
        double closestDistance = Double.MAX_VALUE;
        double closestX = 0;
        double closestY = 0;

        for (double t = 0; t <= 1.0; t += 0.01) {
            double curveX = cubicCurvePoint(connectionCurve.getStartX(), connectionCurve.getControlX1(), connectionCurve.getControlX2(), connectionCurve.getEndX(), t);
            double curveY = cubicCurvePoint(connectionCurve.getStartY(), connectionCurve.getControlY1(), connectionCurve.getControlY2(), connectionCurve.getEndY(), t);

            double distance = Math.sqrt(Math.pow(mouse.getX() - curveX, 2) + Math.pow(mouse.getY() - curveY, 2));

            if (distance < closestDistance) {
                closestDistance = distance;
                closestX = curveX;
                closestY = curveY;
            }
        }

        // Update the snap point position at the closest point on the visible curve
        removeButton.setTranslateX(closestX);
        removeButton.setTranslateY(closestY);
    }

    // Helper method to calculate cubic Bezier point
    private double cubicCurvePoint(double start, double control1, double control2, double end, double t) {
        double u = 1 - t;
        return Math.pow(u, 3) * start + 3 * Math.pow(u, 2) * t * control1 + 3 * u * Math.pow(t, 2) * control2 + Math.pow(t, 3) * end;
    }

}
