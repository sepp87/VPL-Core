package btscore.graph.connection;

import java.io.File;
import java.io.InputStream;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;
import btscore.utils.FileUtils;

/**
 *
 * @author joostmeulenkamp
 */
public class RemoveButtonView extends Group {

    private static final String xml;
    private static final String svg;

    static {
        xml = FileUtils.readResourceAsString("fontawesome-svg/circle-xmark-solid.svg");
        svg = xml.split("path d=\"")[1].replace("\"/></svg>", "");
    }

    public static final double BUTTON_SIZE = 22;

    private final SVGPath removeIcon;
    private final Circle backgroundCircle;

    public RemoveButtonView() {

        removeIcon = new SVGPath();
        removeIcon.setContent(svg);
        removeIcon.getStyleClass().add("connection-remove-icon");

        /**
         * In JavaFX, SVGPath (a shape that renders an SVG path) does not have
         * an explicit width or height property like ImageView or Rectangle.
         * Instead, its size is determined by the bounds of the SVG path.
         * prefWidth(-1) and prefHeight(-1) return the preferred width and
         * height of the SVGPath, calculated based on its current content (the
         * SVG path itself).
         */
        double width = removeIcon.prefWidth(-1);
        double height = removeIcon.prefHeight(-1);
        double scale = BUTTON_SIZE / width;

        removeIcon.setLayoutX(-width / 2);
        removeIcon.setLayoutY(-height / 2);
        removeIcon.setScaleX(scale);
        removeIcon.setScaleY(scale);

        double radius = (BUTTON_SIZE - 1) / 2;
        backgroundCircle = new Circle(0, 0, radius, Color.WHITE);

        this.getChildren().addAll(backgroundCircle, removeIcon);
        this.setVisible(false);
        this.setMouseTransparent(true);
    }

}
