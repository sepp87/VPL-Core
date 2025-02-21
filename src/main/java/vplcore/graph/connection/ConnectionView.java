package vplcore.graph.connection;

import java.io.File;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.SVGPath;
import vplcore.graph.port.PortModel;
import vplcore.util.FileUtils;
import vplcore.workspace.WorkspaceController;
import vplcore.workspace.WorkspaceView;

/**
 *
 * @author JoostMeulenkamp
 */
public class ConnectionView extends Group {

    private final WorkspaceView workspaceView;

    private final static double SNAPPING_WIDTH = 22;
    private final PortModel startPort;
    private final PortModel endPort;

    private final CubicCurve connectionCurve;
    private final CubicCurve snappingCurve;

    public final DoubleProperty startBezierXProperty = new SimpleDoubleProperty();
    public final DoubleProperty endBezierXProperty = new SimpleDoubleProperty();

    public ConnectionView(WorkspaceController workspaceController, PortModel startPort, PortModel endPort) {
        this.workspaceView = workspaceController.getView();

        this.startPort = startPort;
        this.endPort = endPort;

        connectionCurve = createCurve();
        connectionCurve.getStyleClass().add("connection");

        snappingCurve = createCurve();
        snappingCurve.setFill(null);
        snappingCurve.setStrokeWidth(SNAPPING_WIDTH);
        snappingCurve.setStroke(Color.TRANSPARENT);

        initializeRemoveButton();

    }
    
    public CubicCurve getConnectionCurve() {
        return connectionCurve;
        
    }
    
    public CubicCurve getSnappingCurve() {
        return snappingCurve;
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

    private void showRemoveButton(double workspaceX, double workspaceY) {

        Point2D mouse = new Point2D(workspaceX, workspaceY);

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
