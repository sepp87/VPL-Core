package jo.vpl.core;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

/**
 *
 * @author JoostMeulenkamp
 */
public final class BindingPoint {

    public EventBlaster propertyChanged = new EventBlaster(this);
    private final DoubleProperty x = new SimpleDoubleProperty();
    private final DoubleProperty y = new SimpleDoubleProperty();

    public BindingPoint(double x, double y) {
        this();
        x().setValue(x);
        y().setValue(y);
    }

    public BindingPoint() {
        propertyChanged.set(x());
        propertyChanged.set(y());
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

}
