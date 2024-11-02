package vplcore.workspace;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

/**
 *
 * @author Joost
 */
public class WorkspaceModel {

    public static final double MAX_ZOOM = 1.5;
    public static final double MIN_ZOOM = 0.3;
    public static final double ZOOM_STEP = 0.1;

    private final DoubleProperty zoomFactor;  // Property to hold zoom factor
    private final DoubleProperty translateX;
    private final DoubleProperty translateY;

    public WorkspaceModel() {
        zoomFactor = new SimpleDoubleProperty(1.0); // Default zoom level
        translateX = new SimpleDoubleProperty(0.);
        translateY = new SimpleDoubleProperty(0.);

//        zoomFactor.addListener(this::zoomFactorChanged);
        translateX.addListener(this::translateXChanged);
    }

    private void zoomFactorChanged(Object b, Object o, Object n) {
        System.out.println(n);
    }

    private void translateXChanged(Object b, Object o, Object n) {
//        System.out.println(n +  " ZoomModel");
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

    public void setZoomFactor(double factor) {
        this.zoomFactor.set(Math.round(factor * 10) / 10.);
    }
}
