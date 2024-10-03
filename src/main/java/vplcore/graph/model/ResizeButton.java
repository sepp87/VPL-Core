package vplcore.graph.model;

import javafx.geometry.Point2D;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

/**
 *
 * @author joostmeulenkamp
 */
public class ResizeButton {

    
    
    // because of the inverted Y-axis, the tail is mathematically described like a downward hill:
    //
    // topLeft ___  topRight
    //         \  |
    //          \ |
    //           \| bottomRight
    //
    public static Path buildTail() {

        double height = 30;
        double width = height;
        double radius = 3; 

        double degrees = 45;
        double radians = Math.toRadians(degrees);
        double slope = -Math.tan(radians); // invert the slope, since axis Y is pointing downward
        Point2D vector = new Point2D(slope, -1);
        double length = vector.magnitude();
        Point2D normal = vector.multiply(1 / length);
        Point2D offset = normal.multiply(radius);

        Point2D topLeft = new Point2D(radius, height - radius);
        Point2D bottomRight = new Point2D(width - radius, radius);


        Point2D topLeft1 = topLeft.add(offset);
        Point2D topLeft2 = new Point2D(radius, height);
        Point2D topRight1 = new Point2D(width - radius, height);
        Point2D topRight2 = new Point2D(width, height - radius);
        Point2D bottomRight1 = new Point2D(width, radius);
        Point2D bottomRight2 = bottomRight.add(offset);

        Path tail = new Path();
        tail.getElements().add(new MoveTo(topLeft1.getX(), topLeft1.getY()));
        tail.getElements().add(new ArcTo(radius, radius, 0, topLeft2.getX(), topLeft2.getY(), false, false));
        tail.getElements().add(new LineTo(topRight1.getX(), topRight1.getY()));
        tail.getElements().add(new ArcTo(radius, radius, 0, topRight2.getX(), topRight2.getY(), false, false));
        tail.getElements().add(new LineTo(bottomRight1.getX(), bottomRight1.getY()));
        tail.getElements().add(new ArcTo(radius, radius, 0, bottomRight2.getX(), bottomRight2.getY(), false, false));
        tail.getElements().add(new ClosePath());

        tail.setStyle("-fx-fill: #5F5F5F;-fx-stroke: transparent;-fx-stroke-width: 0px;");
        return tail;
    }
}
