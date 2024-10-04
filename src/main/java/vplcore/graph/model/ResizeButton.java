package vplcore.graph.model;

import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

/**
 *
 * @author joostmeulenkamp
 */
public class ResizeButton extends HBox {

    public ResizeButton() {
        setAlignment(Pos.CENTER_RIGHT);
//        this.setPrefWidth(30);
//        this.setPrefHeight(30);

        Path path = buildChevronRightDown();
        HBox.setMargin(path, new Insets(0, 5, 5, 0));
        getChildren().add(path);
    }

    // Basic geomterical represention, with coordinate denotations:
    //
    //   innerRight  _  outerRight
    //  innerCorner | |
    // innerLeft ___| |
    //          |_____| outerCornerRight
    // outerLeft       outerCornerLeft
    //
    public static Path buildChevronRightDown() {
        double height = 12;
        double width = height;
        double thickness = height / 5; // approximately 1/5 of height for Font Awesome
        double radius = thickness / 2;

        Point2D innerRight = new Point2D(width - thickness, radius);
        Point2D outerRight = new Point2D(width, radius);

        Point2D innerCorner = new Point2D(width - thickness, height - thickness);
        Point2D outerCornerRight = new Point2D(width, height - radius);
        Point2D outerCornerLeft = new Point2D(width - radius, height);

        Point2D innerLeft = new Point2D(radius, height - thickness);
        Point2D outerLeft = new Point2D(radius, height);

        Path chevron = new Path();
        chevron.getElements().add(new MoveTo(innerCorner.getX(), innerCorner.getY()));
        chevron.getElements().add(new LineTo(innerRight.getX(), innerRight.getY()));
        chevron.getElements().add(new ArcTo(radius, radius, 0, outerRight.getX(), outerRight.getY(), false, true));
        chevron.getElements().add(new LineTo(outerCornerRight.getX(), outerCornerRight.getY()));
        chevron.getElements().add(new ArcTo(radius, radius, 0, outerCornerLeft.getX(), outerCornerLeft.getY(), false, true));
        chevron.getElements().add(new LineTo(outerLeft.getX(), outerLeft.getY()));
        chevron.getElements().add(new ArcTo(radius, radius, 0, innerLeft.getX(), innerLeft.getY(), false, true));
        chevron.getElements().add(new ClosePath());

        chevron.setStyle("-fx-fill: #5F5F5F;-fx-stroke: transparent;-fx-stroke-width: 0px;");
        return chevron;
    }

    // Basic geomterical represention, with coordinate denotations:
    //
    //    innerRight  outerRight
    //             /|
    //            / |
    // innerLeft /__| cornerRight
    // outerLeft     cornerLeft
    //
    public static Path buildCaretRightDown() {

        double height = 12;
        double width = height;
        double radius = height / 7.5; // approximately height divided by 7.5 for Font Awesome

        double degrees = 45;
        double radians = Math.toRadians(degrees);
        double slope = -Math.tan(radians); // invert the slope, since axis Y is pointing downward
        Point2D vector = new Point2D(slope, -1);
        double length = vector.magnitude();
        Point2D normal = vector.multiply(1 / length);
        Point2D offset = normal.multiply(radius);

        Point2D topLeft = new Point2D(radius, height - radius);
        Point2D bottomRight = new Point2D(width - radius, radius);

        Point2D innerLeft = topLeft.add(offset);
        Point2D outerLeft = new Point2D(radius, height);
        Point2D cornerLeft = new Point2D(width - radius, height);
        Point2D cornerRight = new Point2D(width, height - radius);
        Point2D outerRight = new Point2D(width, radius);
        Point2D innerRight = bottomRight.add(offset);

        Path caret = new Path();
        caret.getElements().add(new MoveTo(innerLeft.getX(), innerLeft.getY()));
        caret.getElements().add(new ArcTo(radius, radius, 0, outerLeft.getX(), outerLeft.getY(), false, false));
        caret.getElements().add(new LineTo(cornerLeft.getX(), cornerLeft.getY()));
        caret.getElements().add(new ArcTo(radius, radius, 0, cornerRight.getX(), cornerRight.getY(), false, false));
        caret.getElements().add(new LineTo(outerRight.getX(), outerRight.getY()));
        caret.getElements().add(new ArcTo(radius, radius, 0, innerRight.getX(), innerRight.getY(), false, false));
        caret.getElements().add(new ClosePath());

        caret.setStyle("-fx-fill: #5F5F5F;-fx-stroke: transparent;-fx-stroke-width: 0px;");
        return caret;
    }
}
