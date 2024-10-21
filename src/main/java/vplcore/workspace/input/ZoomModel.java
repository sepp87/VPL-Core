package vplcore.workspace.input;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import vplcore.workspace.Workspace;

/**
 *
 * @author Joost
 */
public class ZoomModel {

    public static final double MAX_ZOOM = 1.5;
    public static final double MIN_ZOOM = 0.3;
    public static final double ZOOM_STEP = 0.1;

    private final DoubleProperty zoomFactor;  // Property to hold zoom factor
    private final DoubleProperty translateX;
    private final DoubleProperty translateY;

    public ZoomModel() {
        this.zoomFactor = new SimpleDoubleProperty(1.0); // Default zoom level
        this.translateX = new SimpleDoubleProperty(0.);
        this.translateY = new SimpleDoubleProperty(0.);
    }

    public DoubleProperty zoomFactorProperty() {
        return zoomFactor;
    }

    public DoubleProperty translateXProperty() {
        return translateX;
    }

    public DoubleProperty translateYProperty() {
        return translateY;
    }

    public void resetZoomFactor() {
        zoomFactor.set(1.0);
    }

    // Increment zoom factor by the defined step size
    public double getIncrementedZoomFactor() {
        return Math.min(MAX_ZOOM, zoomFactor.get() + ZOOM_STEP);
    }

    // Decrement zoom factor by the defined step size
    public double getDecrementedZoomFactor() {
        return Math.max(MIN_ZOOM, zoomFactor.get() - ZOOM_STEP);
    }

}
