package vplcore.graph.util;

import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurve;
import vplcore.graph.model.Connection;
import vplcore.workspace.Workspace;

/**
 *
 * @author joostmeulenkamp
 */
public class PortDisconnector {

    private Workspace workspace;
    private Group removeButton;
    private Circle snapPoint;
    private Connection removableConnection;

    public PortDisconnector(Workspace workspace) {
        this.workspace = workspace;
        initializeRemoveButton();
    }

    private void initializeRemoveButton() {
        snapPoint = new Circle(0, 0, 10, Paint.valueOf("RED"));
        snapPoint.setVisible(false);
        snapPoint.setOnMouseClicked(event -> removeConnection());
        workspace.getChildren().add(snapPoint);
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
            if (!event.getPickResult().getIntersectedNode().equals(snapPoint)) {
                hideRemoveButton();
            }
        }
    };
    public final EventHandler<MouseEvent> enteredSnappingCurveHandler = new EventHandler<>() {
        @Override
        public void handle(MouseEvent event) {
            snapPoint.setVisible(true);
            CubicCurve snappingCurve = (CubicCurve) event.getSource();
            removableConnection = (Connection) snappingCurve.getUserData();
        }
    };

    public void showRemoveButton(MouseEvent event) {
        if (!workspace.getChildren().contains(snapPoint)) {
            workspace.getChildren().add(snapPoint);
        }

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
        snapPoint.setCenterX(closestX);
        snapPoint.setCenterY(closestY);
    }

    public void hideRemoveButton() {
        snapPoint.setVisible(false);
    }

    public Circle getRemoveButton() {
        return snapPoint;
    }

    public void removeConnection() {
        removableConnection.removeFromCanvas();
        snapPoint.setVisible(false);
//        Alert alert = new Alert(Alert.AlertType.INFORMATION);
//        alert.setTitle("Snap Point Clicked");
//        alert.setHeaderText(null);
//        alert.setContentText("You clicked on the snap point!");
//        alert.showAndWait();
    }

    // Helper method to calculate cubic Bezier point
    private double cubicCurvePoint(double start, double control1, double control2, double end, double t) {
        double u = 1 - t;
        return Math.pow(u, 3) * start + 3 * Math.pow(u, 2) * t * control1 + 3 * u * Math.pow(t, 2) * control2 + Math.pow(t, 3) * end;
    }

}
