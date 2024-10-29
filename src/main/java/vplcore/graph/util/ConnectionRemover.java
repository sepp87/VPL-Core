package vplcore.graph.util;

import java.io.File;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.SVGPath;
import vplcore.graph.model.Connection;
import vplcore.workspace.Workspace;

/**
 *
 * @author joostmeulenkamp
 */
public class ConnectionRemover {

    private final Workspace workspace;

    private Group removeButton;
    private SVGPath removeIcon;
    private Circle circle;
    private Connection removableConnection;

    public ConnectionRemover(Workspace workspace) {
        this.workspace = workspace;
        initializeRemoveButton();
    }

    private void initializeRemoveButton() {

        String xml = vplcore.Util.readFileAsString(new File(vplcore.Config.get().iconsDirectory() + "circle-xmark-solid.svg"));
        String svg = xml.split("path d=\"")[1].replace("\"/></svg>", "");
        removeIcon = new SVGPath();
        removeIcon.setContent(svg);
        removeIcon.getStyleClass().add("connection-remove-icon");

        double width = removeIcon.prefWidth(-1);
        double height = removeIcon.prefHeight(-1);
        double desiredWidth = Connection.SNAPPING_WIDTH;
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
        workspace.getChildren().add(removeButton);
    }

    public final EventHandler<MouseEvent> movedOnSnappingCurveHandler = new EventHandler<>() {
        @Override
        public void handle(MouseEvent event) {
            showRemoveButton(event);
            event.consume();

        }
    };
    public final EventHandler<MouseEvent> exitedSnappingCurveHandler = new EventHandler<>() {
        @Override
        public void handle(MouseEvent event) {
            Node node = event.getPickResult().getIntersectedNode();
            if (!node.equals(removeIcon) && !node.equals(circle)) {
                hideRemoveButton();
            }
        }
    };
    public final EventHandler<MouseEvent> enteredSnappingCurveHandler = new EventHandler<>() {
        @Override
        public void handle(MouseEvent event) {
            removeButton.setVisible(true);
            CubicCurve snappingCurve = (CubicCurve) event.getSource();
            removableConnection = (Connection) snappingCurve.getUserData();
            snappingCurve.addEventHandler(MouseEvent.MOUSE_CLICKED, clickedSnappingCurveHandler);
        }
    };

    public final EventHandler<MouseEvent> clickedSnappingCurveHandler = new EventHandler<>() {
        @Override
        public void handle(MouseEvent event) {
            removeConnection();
        }
    };

    public void showRemoveButton(MouseEvent event) {

        CubicCurve snappingCurve = (CubicCurve) event.getSource();
        Connection connection = (Connection) snappingCurve.getUserData();
        CubicCurve connectionCurve = connection.connectionCurve;

        Point2D mouse = workspace.sceneToLocal(event.getSceneX(), event.getSceneY());

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

    public void hideRemoveButton() {
        removeButton.setVisible(false);
        if (removableConnection != null) {
            removableConnection.snappingCurve.removeEventHandler(MouseEvent.MOUSE_CLICKED, clickedSnappingCurveHandler);
        }
    }

    public Group getRemoveButton() {
        return removeButton;
    }

    public void removeConnection() {
        removableConnection.removeFromCanvas();
        removeButton.setVisible(false);
    }

    // Helper method to calculate cubic Bezier point
    private double cubicCurvePoint(double start, double control1, double control2, double end, double t) {
        double u = 1 - t;
        return Math.pow(u, 3) * start + 3 * Math.pow(u, 2) * t * control1 + 3 * u * Math.pow(t, 2) * control2 + Math.pow(t, 3) * end;
    }

}
