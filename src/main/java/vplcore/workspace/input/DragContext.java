package vplcore.workspace.input;

import vplcore.EventBlaster;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

/**
 * Mouse drag context used for scene and nodes.
 */
public class DragContext {

    public EventBlaster propertyChanged = new EventBlaster(this);
    private final DoubleProperty x = new SimpleDoubleProperty();
    private final DoubleProperty y = new SimpleDoubleProperty();
    private final DoubleProperty translateX = new SimpleDoubleProperty();
    private final DoubleProperty translateY = new SimpleDoubleProperty();

    public DragContext(double x, double y, double translateX, double translateY) {
        this(x, y);
        translateX().setValue(translateX);
        translateY().setValue(translateY);
    }

    public DragContext(double x, double y) {
        this();
        x().setValue(x);
        y().setValue(y);
    }

    public DragContext() {
        propertyChanged.set(x());
        propertyChanged.set(y());
        propertyChanged.set(translateX());
        propertyChanged.set(translateY());
    }

    public final void setX(double value) {
        x.set(value);
    }

    public final double getX() {
        return x.get();
    }

    public DoubleProperty x() {
        return x;
    }

    public final double getY() {
        return y.get();
    }

    public final void setY(double value) {
        y.set(value);
    }

    public DoubleProperty y() {
        return y;
    }

    public final void setTranslateX(double value) {
        translateX.set(value);
    }

    public final double getTranslateX() {
        return translateX.get();
    }

    public DoubleProperty translateX() {
        return translateX;
    }

    public final double getTranslateY() {
        return translateY.get();
    }

    public final void setTranslateY(double value) {
        translateY.set(value);
    }

    public DoubleProperty translateY() {
        return translateY;
    }

}
