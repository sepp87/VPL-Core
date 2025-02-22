package vplcore.graph.connection;

import java.io.File;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;
import vplcore.util.FileUtils;

/**
 *
 * @author joostmeulenkamp
 */
public class RemoveButtonView extends Group {

    private static final String xml;
    private static final String svg;

    static {
        xml = FileUtils.readFileAsString(new File(vplcore.Config.get().iconsDirectory() + "circle-xmark-solid.svg"));
        svg = xml.split("path d=\"")[1].replace("\"/></svg>", "");
    }

    public static final double BUTTON_SIZE = 22;

    private final SVGPath removeIcon;
    private final Circle backgroundCircle;

    public RemoveButtonView() {

        removeIcon = new SVGPath();
        removeIcon.setContent(svg);
        removeIcon.getStyleClass().add("connection-remove-icon");

        backgroundCircle = new Circle(0, 0, BUTTON_SIZE / 2, Color.WHITE);

        this.getChildren().addAll(backgroundCircle, removeIcon);
        this.setVisible(false);
        this.setMouseTransparent(true);
    }
}
