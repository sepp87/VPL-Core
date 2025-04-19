package btscore.graph.connection;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.CubicCurve;

/**
 *
 * @author JoostMeulenkamp
 */
public class ConnectionView extends Group {

    public static final double SNAPPING_WIDTH = RemoveButtonView.BUTTON_SIZE;

    private final CubicCurve connectionCurve;
    private final CubicCurve snappingCurve;
    private final RemoveButtonView removeButton;

    public ConnectionView() {
        connectionCurve = new CubicCurve();
        connectionCurve.getStyleClass().add("connection");

        snappingCurve = new CubicCurve();
        snappingCurve.setFill(null);
        snappingCurve.setStrokeWidth(SNAPPING_WIDTH);
        snappingCurve.setStroke(Color.TRANSPARENT);

        removeButton = new RemoveButtonView();

        this.getChildren().addAll(connectionCurve, snappingCurve, removeButton);
    }

    public CubicCurve getConnectionCurve() {
        return connectionCurve;
    }

    public CubicCurve getSnappingCurve() {
        return snappingCurve;
    }

    public RemoveButtonView getRemoveButton() {
        return removeButton;
    }
}
